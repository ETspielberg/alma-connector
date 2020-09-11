package org.unidue.ub.libintel.almaconnector.controller;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.unidue.ub.alma.shared.acq.Invoice;
import org.unidue.ub.alma.shared.acq.Vendor;
import org.unidue.ub.libintel.almaconnector.model.SapData;
import org.unidue.ub.libintel.almaconnector.model.run.AlmaExportRun;
import org.unidue.ub.libintel.almaconnector.model.run.SapResponseRun;
import org.unidue.ub.libintel.almaconnector.service.AlmaExportRunService;
import org.unidue.ub.libintel.almaconnector.service.AlmaInvoiceServices;
import org.unidue.ub.libintel.almaconnector.service.FileWriterService;
import org.unidue.ub.libintel.almaconnector.service.VendorService;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.unidue.ub.libintel.almaconnector.Utils.*;

/**
 * Controller defining the endpoints for retrieving the invoices.
 */
@Controller
public class InvoiceController {

    private final AlmaInvoiceServices almaInvoiceServices;

    private final VendorService vendorService;

    private final FileWriterService fileWriterService;

    private final AlmaExportRunService almaExportRunService;

    private final static Logger log = LoggerFactory.getLogger(InvoiceController.class);

    /**
     * constructor based autowiring to the invoice service, the filewriter service and the vendorservice.
     *
     * @param almaInvoiceServices the invoice service bean
     * @param vendorService       the vendor service bean
     * @param fileWriterService   the file writer service
     */
    InvoiceController(AlmaInvoiceServices almaInvoiceServices,
                      VendorService vendorService,
                      FileWriterService fileWriterService,
                      AlmaExportRunService almaExportRunService) {
        this.almaInvoiceServices = almaInvoiceServices;
        this.vendorService = vendorService;
        this.fileWriterService = fileWriterService;
        this.almaExportRunService = almaExportRunService;
    }

    /**
     * the overview page of the
     * @return the string sap for to use the sap.html template
     */
    @GetMapping("/sap")
    public String getSapPage(Model model) {
        AlmaExportRun almaExportRun = this.almaExportRunService.getAlmaExportRun(new Date(), "E0001");
        model.addAttribute("almaExportRun", almaExportRun);
        return "sap";
    }

    @PostMapping("/collectInvoices")
    public String collectInvoices(@ModelAttribute("almaExportRun") AlmaExportRun almaExportRun, Model model) {
        log.debug(String.format("collecting invoices for %s at %s", dateformat.format(almaExportRun.getDesiredDate()), almaExportRun.getInvoiceOwner()));
        AlmaExportRun almaExportRunNew = this.almaExportRunService.getAlmaExportRun(almaExportRun.getDesiredDate(), almaExportRun.getInvoiceOwner());
        almaExportRunNew.setDateSpecific(almaExportRun.isDateSpecific());
        log.info(almaExportRunNew.log());
        this.almaExportRunService.saveAlmaExportRun(almaExportRunNew);
        almaExportRunNew = this.almaInvoiceServices.getInvoices(almaExportRunNew);
        log.info(almaExportRunNew.log());
        for (Invoice invoice : almaExportRunNew.getInvoices()) {
            Vendor vendor = this.vendorService.getVendorAccount(invoice.getVendor().getValue());
            List<SapData> sapDataList = convertInvoiceToSapData(invoice, vendor);
            log.debug(String.format("adding %d SAP data to the list", sapDataList.size()));
            almaExportRunNew.addSapDataList(sapDataList);
            log.debug(String.format("list contains now %d entries",almaExportRunNew.getSapData().size() ));
        }
        almaExportRunNew.sortSapData();
        log.info(almaExportRunNew.log());
        almaExportRunNew = this.fileWriterService.writeAlmaExport(almaExportRunNew);
        model.addAttribute("almaExportRun", almaExportRunNew);
        return "finishedRun";
    }

    /**
     * receives the sap import result as xlsx file and updates the invoices in alma correspondingly
     * @param sapReturnFile the result xlsx file resulting from the SAP import
     * @return returns a status of 200 if the import was successful
     * @throws IOException thrown if the file could not be read
     */
    @PostMapping("/invoicesUpdate")
    public ResponseEntity<?> updateInvoicesWithSapData(@RequestParam("file") MultipartFile sapReturnFile) throws IOException {
        // read the excel spreadsheet from the request
        XSSFWorkbook workbook = new XSSFWorkbook(sapReturnFile.getInputStream());
        // retrieve first sheet
        XSSFSheet worksheet = workbook.getSheetAt(0);

        //convert the excel sheet to a SapResponseRun holding the individual responses
        SapResponseRun container = getFromExcel(worksheet);

        if (container.getNumberOfReadErrors() > 0)
            return ResponseEntity.badRequest().body(container.getNumberOfReadErrors() + "Errors on parsing amount");
        container = this.almaInvoiceServices.updateInvoiceWithErpData(container);
        if (container.getNumberOfErrors() > 0)
            return ResponseEntity.accepted().body(container.getNumberOfErrors() + "Errors on updating invoices: ");
        else
            return ResponseEntity.accepted().build();
    }

    @PostMapping("/showImportFiles")
    public String getImportFiles(@ModelAttribute("almaExportRun") AlmaExportRun almaExportRun, Model model) {
        log.info(String.format("showing files for %s : %s ", dateformat.format(almaExportRun.getDesiredDate()), almaExportRun.isDateSpecific()));
        model.addAttribute("almaExportRun", almaExportRun);
        return "showImportFiles";
    }

    @GetMapping("/downloadFile/{type}/{date}")
    public ResponseEntity<Resource> serveFile(@PathVariable String type, @PathVariable String date) throws FileNotFoundException {
        Resource file = fileWriterService.loadFiles(date, type);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}

package org.unidue.ub.libintel.almaconnector.controller;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
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

    @Value("${sap.home.tax.keys}")
    private List<String> homeTaxKeys;

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
            almaExportRunNew.addSapDataList(sapDataList, homeTaxKeys);
            log.debug(String.format("run contains now %d entries: %d home and %d foreign",
                    almaExportRunNew.getTotalSapData(),
                    almaExportRunNew.getHomeSapData().size(),
                    almaExportRunNew.getForeignSapData().size() ));
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
    public String updateInvoicesWithSapData(@RequestParam("file") MultipartFile sapReturnFile, Model model) throws IOException {
        // read the excel spreadsheet from the request
        XSSFWorkbook workbook = new XSSFWorkbook(sapReturnFile.getInputStream());
        // retrieve first sheet
        XSSFSheet worksheet = workbook.getSheetAt(0);

        //convert the excel sheet to a SapResponseRun holding the individual responses
        SapResponseRun container = getFromExcel(worksheet);
        container = this.almaInvoiceServices.updateInvoiceWithErpData(container);
        model.addAttribute("container", container);
        return "invoicesUpdate";
    }

    @PostMapping("/showImportFiles")
    @DateTimeFormat(pattern = "E MMM dd HH:mm:ss z yyyy")
    public String getImportFiles(@ModelAttribute("almaExportRun") AlmaExportRun almaExportRun, Model model) {
        log.info(String.format("showing files for %s : %s; %d selected ", dateformat.format(almaExportRun.getDesiredDate()), almaExportRun.isDateSpecific(), almaExportRun.getNumberHomeDataSelected()));
        log.info(String.valueOf(almaExportRun.getHomeSapData().get(0).creditor));
        almaExportRun = this.fileWriterService.writeAlmaExport(almaExportRun);
        model.addAttribute("almaExportRun", almaExportRun);
        return "showImportFiles";
    }

    @GetMapping("/downloadFile/{type}/{owner}/{date}")
    public ResponseEntity<Resource> serveFile(@PathVariable String type, @PathVariable String date, @PathVariable String owner) throws FileNotFoundException {
        Resource file = fileWriterService.loadFiles(date, type, owner);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}

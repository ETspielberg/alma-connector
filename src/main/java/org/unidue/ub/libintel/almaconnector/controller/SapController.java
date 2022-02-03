package org.unidue.ub.libintel.almaconnector.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.unidue.ub.alma.shared.acq.Invoice;
import org.unidue.ub.alma.shared.acq.Vendor;
import org.unidue.ub.libintel.almaconnector.model.sap.SapData;
import org.unidue.ub.libintel.almaconnector.model.run.AlmaExportRun;
import org.unidue.ub.libintel.almaconnector.model.run.SapResponseRun;
import org.unidue.ub.libintel.almaconnector.service.SapService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaExportRunService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaVendorService;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.unidue.ub.libintel.almaconnector.service.SapService.*;

/**
 * Controller defining the endpoints for retrieving the invoices.
 */
@Controller
@Slf4j
public class SapController {

    private final AlmaVendorService vendorService;

    private final AlmaExportRunService almaExportRunService;

    private final SapService sapService;

    @Value("${libintel.sap.home.tax.keys}")
    private List<String> homeTaxKeys;

    /**
     * constructor based autowiring to the invoice service, the filewriter service and the vendorservice.
     *
     * @param vendorService       the vendor service bean
     * @param sapService          the sap service
     */
    SapController(AlmaVendorService vendorService,
                  SapService sapService,
                  AlmaExportRunService almaExportRunService) {
        this.vendorService = vendorService;
        this.almaExportRunService = almaExportRunService;
        this.sapService = sapService;
    }


    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setAutoGrowCollectionLimit(Integer.MAX_VALUE);
    }


    /**
     * the overview page of the
     * @return the string sap for to use the sap.html template
     */
    @GetMapping("/sap/start")
    public String getSapPage(Model model) {
        AlmaExportRun almaExportRun = this.almaExportRunService.getAlmaExportRun(new Date(), "E0001");
        model.addAttribute("almaExportRun", almaExportRun);
        return "sap/start";
    }

    /**
     * collects the invoices from the Alma API and transforms them into <code>SapData</code> objects and renders the results into the finshedRun.html
     * @param almaExportRun the container object for this particular run
     * @param model the model object holding the data for the rendered web page
     * @return the finishedRun html page
     */
    @PostMapping("/collectInvoices")
    public String collectInvoices(@ModelAttribute("almaExportRun") AlmaExportRun almaExportRun, Model model) {
        log.debug(String.format("collecting invoices for %s at %s", dateformat.format(almaExportRun.getDesiredDate()), almaExportRun.getInvoiceOwner()));
        AlmaExportRun almaExportRunNew = this.almaExportRunService.getAlmaExportRun(almaExportRun.getDesiredDate(), almaExportRun.getInvoiceOwner());
        almaExportRunNew.setDateSpecific(almaExportRun.isDateSpecific());
        log.info(almaExportRunNew.log());
        this.almaExportRunService.saveAlmaExportRun(almaExportRunNew);
        almaExportRunNew = this.sapService.getInvoices(almaExportRunNew);
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
        model.addAttribute("almaExportRun", almaExportRunNew);
        return "sap/finishedRun";
    }

    @PostMapping("/sap/collectSelectedInvoices")
    public String collectByInvoiceNumbers(@ModelAttribute("almaExportRun") AlmaExportRun almaExportRun, Model model) {
        log.debug(String.format("collecting invoices for %s at %s", dateformat.format(almaExportRun.getDesiredDate()), almaExportRun.getInvoiceOwner()));
        AlmaExportRun almaExportRunNew = this.almaExportRunService.getAlmaExportRun(almaExportRun.getDesiredDate(), almaExportRun.getInvoiceOwner());
        almaExportRunNew.setDateSpecific(almaExportRun.isDateSpecific());
        log.info(almaExportRunNew.log());
        this.almaExportRunService.saveAlmaExportRun(almaExportRunNew);
        almaExportRunNew = this.sapService.getInvoicesForInvoiceNumbers(almaExportRunNew);
        model.addAttribute("almaExportRun", almaExportRunNew);
        return "sap/finishedRun";
    }

    @PostMapping("/sap/availableInvoices")
    public String getAvaliableInvoices(@ModelAttribute("almaExportRun") AlmaExportRun almaExportRun, Model model) {
        log.debug(String.format("collecting invoices for %s at %s", dateformat.format(almaExportRun.getDesiredDate()), almaExportRun.getInvoiceOwner()));
        AlmaExportRun almaExportRunNew = this.almaExportRunService.getAlmaExportRun(almaExportRun.getDesiredDate(), almaExportRun.getInvoiceOwner());
        almaExportRunNew.setDateSpecific(almaExportRun.isDateSpecific());
        log.info(almaExportRunNew.log());
        this.almaExportRunService.saveAlmaExportRun(almaExportRunNew);
        almaExportRunNew = this.sapService.getAvailableInvoices(almaExportRunNew);
        model.addAttribute("almaExportRun", almaExportRunNew);
        return "sap/availableInvoices";

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
        SapResponseRun container = getFromExcel(worksheet, sapReturnFile.getOriginalFilename());
        container = this.sapService.updateInvoiceWithErpData(container);
        model.addAttribute("results", container);
        return "sap/reimportResults";
    }

    /**
     * displays download buttons for the files containing the selected <code>SapData</code> objects
     * @param almaExportRun the container object for this particular run
     * @param model the model object holding the data for the rendered web page
     * @return the showImportFiles html page
     */
    @PostMapping("/showImportFiles")
    @DateTimeFormat(pattern = "E MMM dd HH:mm:ss z yyyy")
    public String getImportFiles(@ModelAttribute("almaExportRun") AlmaExportRun almaExportRun, Model model) {
        log.info(String.format("showing files for %s : %s; %d selected ", dateformat.format(almaExportRun.getDesiredDate()), almaExportRun.isDateSpecific(), almaExportRun.getNumberHomeDataSelected()));
        almaExportRun = this.sapService.writeAlmaExport(almaExportRun);
        model.addAttribute("almaExportRun", almaExportRun);
        return "sap/showImportFiles";
    }

    /**
     * retrieves a file containing the sap import data
     * @param type the type of data to be retrieved (home or foreign)
     * @param date the date of the run
     * @param owner the owner of the invoices
     * @return the file loaded from disc
     * @throws FileNotFoundException thrown if the desired file does not exist.
     */
    @GetMapping("/downloadFile/{type}/{owner}/{date}")
    public ResponseEntity<Resource> serveFile(@PathVariable String type, @PathVariable String date, @PathVariable String owner) throws FileNotFoundException {
        Resource file = sapService.loadFiles(date, type, owner);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").contentType(MediaType.TEXT_PLAIN).body(file);
    }

    /**
     * retrieves a file containing the sap import data
     * @param type the type of data to be retrieved (home or foreign)
     * @param owner the owner of the invoices
     * @return the file loaded from disc
     * @throws FileNotFoundException thrown if the desired file does not exist.
     */
    @GetMapping("/downloadFile/{type}/{owner}")
    public ResponseEntity<Resource> serveFileForToday(@PathVariable String type, @PathVariable String owner) throws FileNotFoundException {
        Resource file = sapService.loadFilesForToday(type, owner);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").contentType(MediaType.TEXT_PLAIN).body(file);
    }
}

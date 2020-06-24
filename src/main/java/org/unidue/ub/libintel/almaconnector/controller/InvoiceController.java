package org.unidue.ub.libintel.almaconnector.controller;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.unidue.ub.alma.shared.acq.Invoice;
import org.unidue.ub.alma.shared.acq.Vendor;
import org.unidue.ub.libintel.almaconnector.model.SapData;
import org.unidue.ub.libintel.almaconnector.model.SapResponseContainer;
import org.unidue.ub.libintel.almaconnector.service.AlmaInvoiceServices;
import org.unidue.ub.libintel.almaconnector.service.FileWriterService;
import org.unidue.ub.libintel.almaconnector.service.VendorService;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.unidue.ub.libintel.almaconnector.Utils.convertInvoiceToSapData;
import static org.unidue.ub.libintel.almaconnector.Utils.getFromExcel;

/**
 * Controller defining the endpoints for retrieving the invoices.
 */
@Controller
public class InvoiceController {

    private final AlmaInvoiceServices almaInvoiceServices;

    private final VendorService vendorService;

    private final FileWriterService fileWriterService;

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private final Logger log = LoggerFactory.getLogger(InvoiceController.class);

    /**
     * constructor based autowiring to the invoice service, the filewriter service and the vendorservice.
     *
     * @param almaInvoiceServices the invoice service bean
     * @param vendorService       the vendor service bean
     * @param fileWriterService   the file writer service
     */
    InvoiceController(AlmaInvoiceServices almaInvoiceServices, VendorService vendorService, FileWriterService fileWriterService) {
        this.almaInvoiceServices = almaInvoiceServices;
        this.vendorService = vendorService;
        this.fileWriterService = fileWriterService;
    }

    /**
     * retrieves the active invoices
     *
     * @return a string representing the success of the file writing.
     */
    @GetMapping("/invoicesActive")
    public ResponseEntity<String> getInvoiceLines() {
        // collect the open invoices from the alma API
        List<Invoice> invoices = this.almaInvoiceServices.getOpenInvoices();
        // set the current date
        String date = dateFormat.format(new Date());
        // write the SAP data to the file
        int missed = writeSapData(invoices, date);

        if (missed == 0)
            return ResponseEntity.ok("all itmes have been successfully written to file");
        else
            return ResponseEntity.ok(missed + " items could not be written");
    }

    /**
     * retrieves the active invoices
     *
     * @return a string representing the success of the file writing.
     */
    @GetMapping("/invoicesByDate")
    public String getByDatePage() {
        return "invoiceByDate";
    }

    /**
     * retrieves the active invoices
     *
     * @return a string representing the success of the file writing.
     */
    @PostMapping("/invoicesByDate")
    public ResponseEntity<String> getInvoiceLineForDate(String date) throws ParseException {
        // set the date to the desired date
        Date dateToSearch = dateFormat.parse(date);
        // collect to open invoices from the alma for a given date
        List<Invoice> invoices = this.almaInvoiceServices.getOpenInvoicesForDate(dateToSearch);
        // write the SAP data to the file
        int missed = writeSapData(invoices, date);
        if (missed == 0)
            return ResponseEntity.ok("all items have been successfully written to file");
        else
            return ResponseEntity.ok(missed + " items could not be written");
    }

    /**
     * private function to write the SAP import file and the check file
     * @param invoices the list of invoices to be written
     * @param date the date for which the data shall be written
     * @return the number of invoices for which no files could be written
     */
    private int writeSapData(List<Invoice> invoices, String date) {
        List<SapData> sapDataList = new ArrayList<>();
        for (Invoice invoice : invoices) {
            Vendor vendor = this.vendorService.getVendorAccount(invoice.getVendor().getValue());
            sapDataList.addAll(convertInvoiceToSapData(invoice, vendor));
        }
        Collections.sort(sapDataList);
        return this.fileWriterService.writeLines(sapDataList, date);
    }

    /**
     * receives the sap import result as xlsx file and updates the invoices in alma correspondingly
     * @param sapReturnFile the result xlsx file resulting from the SAP import
     * @return returns a status of 200 if the import was successful
     * @throws IOException thrown if the file could not be read
     */
    @PostMapping("/invoices/update")
    public ResponseEntity<?> updateInvoicesWithSapData(@RequestParam("file") MultipartFile sapReturnFile) throws IOException {
        // read the excel spreadsheet from the request
        XSSFWorkbook workbook = new XSSFWorkbook(sapReturnFile.getInputStream());
        // retrieve first sheet
        XSSFSheet worksheet = workbook.getSheetAt(0);

        SapResponseContainer container = getFromExcel(worksheet);
        // initialize the error counter
        long numberOfErrors = 0;
        // go through all lines except the first one (the headers) and the last one (summary of all invoices).
        if (container.getNumberOfErrors() > 0)
            return ResponseEntity.badRequest().body(numberOfErrors + "Errors on parsing amount");
        container = this.almaInvoiceServices.updateInvoiceWithErpData(container);
        if (container.getNumberOfErrors() > 0)
            return ResponseEntity.accepted().body(numberOfErrors + "Errors on updating invoices: ");
        else
            return ResponseEntity.accepted().build();
    }
}

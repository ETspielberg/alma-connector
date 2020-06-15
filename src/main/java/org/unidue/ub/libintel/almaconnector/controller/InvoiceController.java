package org.unidue.ub.libintel.almaconnector.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.unidue.ub.alma.shared.acq.Invoice;
import org.unidue.ub.alma.shared.acq.Vendor;
import org.unidue.ub.libintel.almaconnector.model.SapData;
import org.unidue.ub.libintel.almaconnector.service.AlmaInvoiceServices;
import org.unidue.ub.libintel.almaconnector.service.FileWriterService;
import org.unidue.ub.libintel.almaconnector.service.VendorService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.unidue.ub.libintel.almaconnector.Utils.convertInvoiceToSapData;

@Controller
public class InvoiceController {

    private final AlmaInvoiceServices almaInvoiceServices;

    private final VendorService vendorService;

    private final FileWriterService fileWriterService;

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * constructor based autowiring to the invoice service, the filewriter service and the vendorservice.
     * @param almaInvoiceServices the invoice service bean
     * @param vendorService the vendor service bean
     * @param fileWriterService the file writer service
     */
    InvoiceController(AlmaInvoiceServices almaInvoiceServices, VendorService vendorService, FileWriterService fileWriterService) {
        this.almaInvoiceServices = almaInvoiceServices;
        this.vendorService = vendorService;
        this.fileWriterService = fileWriterService;
    }

    /**
     * retrieves the active invoices
     * @return a string representing the success of the file writing.
     */
    @GetMapping("/invoices/active")
    public ResponseEntity<String> getInvoiceLines() {
        List<Invoice> invoices = this.almaInvoiceServices.getOpenInvoices();
        String date = dateFormat.format(new Date());
        int missed = writeSapData(invoices, date);
        if (missed == 0)
            return ResponseEntity.ok("all itmes have been successfully written to file");
        else
            return ResponseEntity.ok(missed + " items could not be written");
    }

    /**
     * retrieves the active invoices
     * @return a string representing the success of the file writing.
     */
    @GetMapping("/invoices/active/bydate")
    public ResponseEntity<String> getInvoiceLineForDate(@RequestParam String date) throws ParseException {
        Date dateToSearch = dateFormat.parse(date);
        List<Invoice> invoices = this.almaInvoiceServices.getOpenInvoicesForDate(dateToSearch);
        int missed = writeSapData(invoices, date);
        if (missed == 0)
            return ResponseEntity.ok("all items have been successfully written to file");
        else
            return ResponseEntity.ok(missed + " items could not be written");
    }

    private int writeSapData(List<Invoice> invoices, String date) {
        List<SapData> sapDataList = new ArrayList<>();
        for (Invoice invoice: invoices) {
            Vendor vendor = this.vendorService.getVendorAccount(invoice.getVendor().getValue());
            sapDataList.addAll(convertInvoiceToSapData(invoice, vendor));
        }
        Collections.sort(sapDataList);
        return this.fileWriterService.writeLines(sapDataList, date);
    }
}

package org.unidue.ub.libintel.almaconnector.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.unidue.ub.alma.shared.acq.Invoice;
import org.unidue.ub.alma.shared.acq.Vendor;
import org.unidue.ub.libintel.almaconnector.clients.acquisition.AlmaVendorApiClient;
import org.unidue.ub.libintel.almaconnector.model.SapData;
import org.unidue.ub.libintel.almaconnector.service.AlmaInvoiceServices;
import org.unidue.ub.libintel.almaconnector.service.FileWriterService;
import org.unidue.ub.libintel.almaconnector.service.VendorService;


import java.util.ArrayList;
import java.util.List;

import static org.unidue.ub.libintel.almaconnector.utils.convertInvoiceToSapData;

@Controller
public class InvoiceController {

    private final AlmaInvoiceServices almaInvoiceServices;

    private final VendorService vendorService;

    private final FileWriterService fileWriterService;

    private final static Logger log = LoggerFactory.getLogger(InvoiceController.class);

    InvoiceController(AlmaInvoiceServices almaInvoiceServices, VendorService vendorService, FileWriterService fileWriterService) {
        this.almaInvoiceServices = almaInvoiceServices;
        this.vendorService = vendorService;
        this.fileWriterService = fileWriterService;
    }

    @GetMapping("/invoices/active")
    public ResponseEntity<String> getInvoiceLines() {
        List<Invoice> invoices = this.almaInvoiceServices.getOpenInvoices();
        List<SapData> sapDataList = new ArrayList<>();
        for (Invoice invoice: invoices) {
            Vendor vendor = this.vendorService.getVendorAccount(invoice.getVendor().getValue());
            sapDataList.addAll(convertInvoiceToSapData(invoice, vendor));
        }
        this.fileWriterService.writeLines(sapDataList);
        return ResponseEntity.ok("finished");
    }
}

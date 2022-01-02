package org.unidue.ub.libintel.almaconnector.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unidue.ub.libintel.almaconnector.model.run.SapDataRun;
import org.unidue.ub.libintel.almaconnector.model.sap.AvailableInvoice;
import org.unidue.ub.libintel.almaconnector.service.SapService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaInvoiceService;

import java.util.List;

@RestController
@RequestMapping("/sap/api/v1/")
@Slf4j
public class SapRestController {

    private final SapService sapService;

    private final AlmaInvoiceService almaInvoiceService;

    /**
     * constructor based autowiring to the invoice service, the filewriter service and the vendorservice.
     *
     * @param sapService          the sap service
     */
    SapRestController(SapService sapService,
                      AlmaInvoiceService almaInvoiceService) {
        this.sapService = sapService;
        this.almaInvoiceService = almaInvoiceService;
    }

    @GetMapping("availableInvoices/{invoiceOwner}")
    public ResponseEntity<List<AvailableInvoice>> getAvailableInvoices(@PathVariable String invoiceOwner) {
        return ResponseEntity.ok(this.almaInvoiceService.getAvailableInvoices(invoiceOwner));

    }

    @GetMapping("updateInvoice/{invoiceNumber}")
    public ResponseEntity<AvailableInvoice> updateAvailableInvoice(@PathVariable String invoiceNumber) {
        return ResponseEntity.ok(new AvailableInvoice(this.almaInvoiceService.retrieveInvoice(invoiceNumber)));
    }

    @GetMapping("sapDataRun/{invoiceOwner}")
    public ResponseEntity<SapDataRun> getAlmaExportRun(@PathVariable String invoiceOwner) {
        SapDataRun sapDataRun = new SapDataRun(invoiceOwner);
        this.sapService.addInvoices(sapDataRun);
        this.sapService.addSapData(sapDataRun);
        return ResponseEntity.ok(sapDataRun);
    }

    @GetMapping("reloadSingleInvoice/{invoiceNumber}")
    public ResponseEntity<SapDataRun> getSingleInvoiceData(@PathVariable String invoiceNumber) {
        SapDataRun sapDataRun = new SapDataRun(invoiceNumber);
        this.sapService.addSingleInvoice(sapDataRun);
        this.sapService.addSapData(sapDataRun);
        return ResponseEntity.ok(sapDataRun);
    }

    @PostMapping("prepareInputFiles")
    public ResponseEntity<String> prepareInputFiles(@RequestBody SapDataRun sapDataRun) {
        try {
            this.sapService.writeExportFiles(sapDataRun);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("collectSapDataForInvoices")
    public  ResponseEntity<SapDataRun> collectSapDataForInvoices(@RequestBody List<AvailableInvoice> invoices) {
        SapDataRun sapDataRun = this.sapService.addManualInvoices(invoices);
        this.sapService.addSapData(sapDataRun);
        return ResponseEntity.ok(sapDataRun);
    }
}

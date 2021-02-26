package org.unidue.ub.libintel.almaconnector.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.unidue.ub.libintel.almaconnector.clients.analytics.AlmaAnalyticsReportClient;
import org.unidue.ub.libintel.almaconnector.configuration.MappingTables;
import org.unidue.ub.libintel.almaconnector.model.analytics.*;
import org.unidue.ub.libintel.almaconnector.service.ScheduledService;


import java.io.IOException;

@Controller
@RequestMapping("/test")
public class TestController {

    private final MappingTables mappingTables;;

    private final AlmaAnalyticsReportClient almaAnalyticsReportClient;

    public final ScheduledService scheduledService;

    private final Logger log = LoggerFactory.getLogger(TestController.class);

    TestController(AlmaAnalyticsReportClient almaAnalyticsReportClient,
                   MappingTables mappingTables,
                   ScheduledService scheduledService) {
        this.almaAnalyticsReportClient = almaAnalyticsReportClient;
        this.mappingTables = mappingTables;
        this.scheduledService = scheduledService;
    }

    @GetMapping("/invoices")
    public ResponseEntity<InvoiceForPaymentReport> getInvoicesForPayment() throws IOException {
        return ResponseEntity.ok(this.almaAnalyticsReportClient.getReport(InvoiceForPayment.PATH, InvoiceForPaymentReport.class));
    }

    @GetMapping("/newItemsWithFunds")
    public ResponseEntity<NewItemWithFundReport> getNewItemsWithFund() throws IOException {
        return ResponseEntity.ok(this.almaAnalyticsReportClient.getReport(NewItemWithOrderLine.PATH, NewItemWithFundReport.class));
    }

    @GetMapping("/mapping/itemStatisticNote")
    public ResponseEntity<String> getMappingValue(String key) {
        log.info(key);
        log.info(String.valueOf(mappingTables.getItemStatisticNote().size()));
        return ResponseEntity.ok(mappingTables.getItemStatisticNote().get(key));
    }

    @GetMapping("/updateStatistics")
    public ResponseEntity<?> updateStatistics() throws IOException {
        this.scheduledService.updateStatisticField();
        return ResponseEntity.ok().build();
    }
}

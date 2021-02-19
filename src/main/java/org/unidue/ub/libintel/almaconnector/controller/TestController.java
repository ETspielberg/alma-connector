package org.unidue.ub.libintel.almaconnector.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.unidue.ub.libintel.almaconnector.clients.analytics.AlmaAnalyticsReportClient;
import org.unidue.ub.libintel.almaconnector.model.analytics.AnalyticsResult;
import org.unidue.ub.libintel.almaconnector.model.analytics.InvoiceForPayment;
import org.unidue.ub.libintel.almaconnector.model.analytics.NewItemWithFund;


import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/test")
public class TestController {

    private final AlmaAnalyticsReportClient almaAnalyticsReportClient;

    TestController(AlmaAnalyticsReportClient almaAnalyticsReportClient) {
        this.almaAnalyticsReportClient = almaAnalyticsReportClient;
    }

    @GetMapping("/invoices")
    public ResponseEntity<List<InvoiceForPayment>> getInvoicesForPayment() throws IOException {
        AnalyticsResult<InvoiceForPayment> analyticsResult = new AnalyticsResult<>();
        return ResponseEntity.ok(this.almaAnalyticsReportClient.getReport(InvoiceForPayment.PATH, (Class<AnalyticsResult<InvoiceForPayment>>) analyticsResult.getClass()).getRows());
    }

    @GetMapping("/newItemsWithFunds")
    public ResponseEntity<List<NewItemWithFund>> getNewItemsWithFund() throws IOException {
        AnalyticsResult<NewItemWithFund> analyticsResult = new AnalyticsResult<>();
        return ResponseEntity.ok(this.almaAnalyticsReportClient.getReport(NewItemWithFund.PATH, (Class<AnalyticsResult<NewItemWithFund>>) analyticsResult.getClass()).getRows());

    }
}

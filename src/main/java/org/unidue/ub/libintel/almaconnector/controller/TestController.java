package org.unidue.ub.libintel.almaconnector.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.unidue.ub.libintel.almaconnector.clients.analytics.AlmaAnalyticsReportClient;
import org.unidue.ub.libintel.almaconnector.model.analytics.InvoiceForPayment;
import org.unidue.ub.libintel.almaconnector.model.analytics.InvoicesForPaymentAnalyticsResult;


import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/test")
public class TestController {

    private final AlmaAnalyticsReportClient almaAnalyticsReportClient;

    private final static String reportPath = "/shared/Universität Duisburg-Essen 49HBZ_UDE/Rechnungen zur Bezahlung";

    TestController(AlmaAnalyticsReportClient almaAnalyticsReportClient) {
        this.almaAnalyticsReportClient = almaAnalyticsReportClient;
    }

    @GetMapping("/invoices")
    public ResponseEntity<List<InvoiceForPayment>> getInvoicesForPayment() throws IOException {
        return ResponseEntity.ok(this.almaAnalyticsReportClient.getReport(reportPath, InvoicesForPaymentAnalyticsResult.class).getRows());

    }
}

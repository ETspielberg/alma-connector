package org.unidue.ub.libintel.almaconnector.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.unidue.ub.libintel.almaconnector.clients.acquisition.AlmaAnalyticsReportClient;

import java.io.IOException;

/**
 * the simple page controllers
 */
@Controller
public class StartController {


    private final AlmaAnalyticsReportClient almaAnalyticsReportClient;

    StartController(AlmaAnalyticsReportClient almaAnalyticsReportClient) {
        this.almaAnalyticsReportClient = almaAnalyticsReportClient;
    }

    /**
     * displys the start page of the alma microservice
     * @return the start html page
     */
    @GetMapping("/start")
    public String getStartPage() {
        return "start";
    }

    @GetMapping("/itemLoanedReport")
    public ResponseEntity<?> getItemLoanedReport() throws IOException {
        return ResponseEntity.ok(this.almaAnalyticsReportClient.getReport());
    }
}

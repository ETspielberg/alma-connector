package org.unidue.ub.libintel.almaconnector.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.unidue.ub.libintel.almaconnector.clients.analytics.AlmaAnalyticsReportClient;
import org.unidue.ub.libintel.almaconnector.model.ItemLoanedReport;

import java.io.IOException;

/**
 * the simple page controllers
 */
@Controller
public class StartController {

    /**
     * displys the start page of the alma microservice
     * @return the start html page
     */
    @GetMapping("/start")
    public String getStartPage() {
        return "start";
    }
}

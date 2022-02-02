package org.unidue.ub.libintel.almaconnector.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.unidue.ub.libintel.almaconnector.clients.alma.analytics.AnalyticsNotRetrievedException;
import org.unidue.ub.libintel.almaconnector.service.LogService;
import org.unidue.ub.libintel.almaconnector.service.RegalfinderService;
import org.unidue.ub.libintel.almaconnector.service.SaveDataService;
import org.unidue.ub.libintel.almaconnector.service.ScheduledService;

import java.io.IOException;

/**
 * starting specific jobs from the scheduled service or hooks
 */
@Controller
@RequestMapping("/services")
public class ServiceController {

    private final ScheduledService scheduledService;

    private final RegalfinderService regalfinderService;

    private final SaveDataService saveDataService;

    private final LogService logService;

    /**
     * constructor based autowiring
     * @param scheduledService the service for the scheduled job
     * @param regalfinderService the service checking the regalfinder
     * @param saveDataService the service saving data for preservation purposes
     * @param logService the service handling the logging and error handling
     */
    ServiceController(ScheduledService scheduledService,
                      RegalfinderService regalfinderService,
                      SaveDataService saveDataService,
                      LogService logService) {
        this.scheduledService = scheduledService;
        this.regalfinderService = regalfinderService;
        this.saveDataService = saveDataService;
        this.logService = logService;
    }

    @GetMapping("/collectRequests")
    private ResponseEntity<?> collectRequests() {
        this.scheduledService.collectRequests();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/testRegalfinder")
    private ResponseEntity<?> testRegalfinder(String collection, String shelfmark) throws IOException {
        boolean response = this.regalfinderService.checkRegalfinder(collection, shelfmark);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/usersToEnd")
    private ResponseEntity<?> updateUsersToEnd(){
        this.scheduledService.runEndingUserNotificationJob();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/saveDailyUserFineFees")
    public ResponseEntity<?> saveDailyUserFineFees() {
        try {
            this.saveDataService.saveDailyUserFineFees();
        } catch (AnalyticsNotRetrievedException analyticsNotRetrievedException) {
            logService.handleAnalyticsException(analyticsNotRetrievedException);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/saveInitialUserFineFees")
    public ResponseEntity<?> saveInitialUserFineFees() {
        try {
            this.saveDataService.saveInitialUserFineFees();
        } catch (AnalyticsNotRetrievedException analyticsNotRetrievedException) {
            logService.handleAnalyticsException(analyticsNotRetrievedException);
        }
        return ResponseEntity.ok().build();
    }
}

package org.unidue.ub.libintel.almaconnector.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.unidue.ub.libintel.almaconnector.service.RegalfinderService;
import org.unidue.ub.libintel.almaconnector.service.SaveDataService;
import org.unidue.ub.libintel.almaconnector.service.ScheduledService;

import java.io.IOException;

@Controller
@RequestMapping("/services")
public class ServiceController {

    private final ScheduledService scheduledService;

    private final RegalfinderService regalfinderService;

    private final SaveDataService saveDataService;

    ServiceController(ScheduledService scheduledService,
                      RegalfinderService regalfinderService,
                      SaveDataService saveDataService) {
        this.scheduledService = scheduledService;
        this.regalfinderService = regalfinderService;
        this.saveDataService = saveDataService;

    }

    @GetMapping("/collectRequests")
    private ResponseEntity<?> collectRequests() throws IOException {
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
    public ResponseEntity<?> saveDailyUserFineFees() throws IOException {
        this.saveDataService.saveDailyUserFineFees();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/saveInitialUserFineFees")
    public ResponseEntity<?> saveInitialUserFineFees() {
        this.saveDataService.saveInitialUserFineFees();
        return ResponseEntity.ok().build();
    }
}

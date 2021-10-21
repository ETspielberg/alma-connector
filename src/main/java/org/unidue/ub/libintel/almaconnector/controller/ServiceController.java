package org.unidue.ub.libintel.almaconnector.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.unidue.ub.alma.shared.user.AlmaUser;
import org.unidue.ub.libintel.almaconnector.service.RegalfinderService;
import org.unidue.ub.libintel.almaconnector.service.ScheduledService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaSetService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaUserService;

import java.io.IOException;

@Controller
@RequestMapping("/services")
public class ServiceController {

    private final AlmaSetService almaSetService;

    private final AlmaUserService almaUserService;

    private final ScheduledService scheduledService;

    private final RegalfinderService regalfinderService;

    ServiceController(AlmaSetService almaSetService,
                      AlmaUserService almaUserService,
                      ScheduledService scheduledService,
                      RegalfinderService regalfinderService) {
        this.almaSetService = almaSetService;
        this.almaUserService = almaUserService;
        this.scheduledService = scheduledService;
        this.regalfinderService = regalfinderService;
    }

    @GetMapping("/scan")
    private String showScanOutForm(Model model) {
        model.addAttribute("setId", "");
        return "services/scan";
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

    @GetMapping("/bereitstellungen")
    private String getHolds(String userId, Model model) throws UserIdInvalidException, UserNotFoundException {
        if ((userId == null) || !userId.trim().matches("[a-zA-Z0-9\\-]+")) {
            throw new UserIdInvalidException("User ID is not valid");
        }
        AlmaUser almaUser = almaUserService.getUser(userId);
        if (almaUser == null)
            throw new UserNotFoundException("Alma User not found");
        return "bereitstellungen";
    }

    private static class UserIdInvalidException extends RuntimeException {
        UserIdInvalidException(String message) { super(message); }

    }

    private static class UserNotFoundException extends RuntimeException {
        UserNotFoundException(String message) { super(message); }
    }
}

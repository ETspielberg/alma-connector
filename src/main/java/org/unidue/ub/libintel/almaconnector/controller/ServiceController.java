package org.unidue.ub.libintel.almaconnector.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.unidue.ub.alma.shared.user.AlmaUser;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaSetService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaUserService;

@Controller
@RequestMapping("/services")
public class ServiceController {

    private final AlmaSetService almaSetService;

    private final AlmaUserService almaUserService;

    ServiceController(AlmaSetService almaSetService,
                      AlmaUserService almaUserService) {
        this.almaSetService = almaSetService;
        this.almaUserService = almaUserService;
    }

    @PostMapping("/scanOut/{setId}")
    private ResponseEntity<?> receiveSet(@PathVariable String setId) {
        boolean success = this.almaSetService.scanInSet(setId, true);
        if (success)
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.badRequest().build();
    }

    @GetMapping("/bereitstellungen")
    private String getHolds(String userId, Model model) throws UserIdInvalidException, UserNotFoundException {
        if ((userId == null) || !userId.trim().matches("[a-zA-Z0-9\\-]+")) {
            throw new UserIdInvalidException();
        }
        AlmaUser almaUser = almaUserService.getUser(userId);
        if (almaUser == null)
            throw new UserNotFoundException();
        return "bereitstellungen";
    }

    private class UserIdInvalidException extends Throwable {
    }

    private class UserNotFoundException extends Throwable {
    }
}

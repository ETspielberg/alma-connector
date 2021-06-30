package org.unidue.ub.libintel.almaconnector.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaSetService;

@Controller
@RequestMapping("/services")
public class ServiceController {

    private final AlmaSetService almaSetService;

    ServiceController(AlmaSetService almaSetService) {
        this.almaSetService = almaSetService;
    }

    @PostMapping("/scanOut/{setId}")
    private ResponseEntity<?> receiveSet(@PathVariable String setId) {
        boolean success = this.almaSetService.scanInSet(setId, true);
        if (success)
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.badRequest().build();
    }
}

package org.unidue.ub.libintel.almaconnector.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.unidue.ub.alma.shared.acq.PoLine;
import org.unidue.ub.libintel.almaconnector.service.AlmaPoLineService;

import java.util.List;

@Controller
public class PoLineController {

    private final AlmaPoLineService almaPoLineService;

    PoLineController(AlmaPoLineService almaPoLineService) {
        this.almaPoLineService = almaPoLineService;
    }

    @GetMapping("/polines/active")
    public ResponseEntity<List<PoLine>> getActivePoLines() {
        return ResponseEntity.ok(this.almaPoLineService.getOpenPoLines());
    }
}

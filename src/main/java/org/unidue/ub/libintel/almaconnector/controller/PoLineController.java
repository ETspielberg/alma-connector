package org.unidue.ub.libintel.almaconnector.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.unidue.ub.alma.shared.acq.PoLine;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaPoLineService;

import java.util.List;

/**
 * Controller defining the endpoints for retrieving the po lines.
 */
@Controller
public class PoLineController {

    private final AlmaPoLineService almaPoLineService;

    /**
     * constructor based autowiring to the po line service
     * @param almaPoLineService the po line service
     */
    PoLineController(AlmaPoLineService almaPoLineService) {
        this.almaPoLineService = almaPoLineService;
    }

    /**
     * retrieves the active PO lines
     * @return a response entity holding a list of PoLine objects
     */
    @GetMapping("/polines/active")
    public ResponseEntity<List<PoLine>> getActivePoLines() {
        return ResponseEntity.ok(this.almaPoLineService.getOpenPoLines());
    }
}

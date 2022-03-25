package org.unidue.ub.libintel.almaconnector.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.unidue.ub.libintel.almaconnector.model.media.elasticsearch.EsPrintManifestation;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaCatalogService;

@Controller
@RequestMapping("/manifestation/api/v1/")
public class AlmaLoansRestController {

    private final AlmaCatalogService almaCatalogService;

    AlmaLoansRestController(AlmaCatalogService almaCatalogService) {
        this.almaCatalogService = almaCatalogService;
    }

    @GetMapping("{mmsId}")
    public ResponseEntity<EsPrintManifestation> getManifestation(@PathVariable String mmsId) {
        return ResponseEntity.ok(this.almaCatalogService.getOverview(mmsId));
    }
}

package org.unidue.ub.libintel.almaconnector.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unidue.ub.libintel.almaconnector.model.jobs.ShibbolethData;
import org.unidue.ub.libintel.almaconnector.service.ShibbolethDataService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shibboleth")
public class ShibbolethController {

    private final ShibbolethDataService shibbolethDataService;

    /**
     * constructor based autowiring of the shibboleth data service
     * @param shibbolethDataService the shibboleth data service
     */
    public ShibbolethController(ShibbolethDataService shibbolethDataService) {
        this.shibbolethDataService = shibbolethDataService;
    }

    @GetMapping("/{platform}")
    public ResponseEntity<ShibbolethData> getShibbolethData(@PathVariable String platform) {
        return ResponseEntity.ok(shibbolethDataService.getDataForPlatform(platform));
    }

    @GetMapping("")
    public ResponseEntity<List<ShibbolethData>> getAllShibbolethData() {
        return ResponseEntity.ok(this.shibbolethDataService.getAllShibbolethData());
    }

    @DeleteMapping("/{platform}")
    public ResponseEntity<?> deletePlatform(@PathVariable String platform) {
        this.shibbolethDataService.delete(platform);
        return ResponseEntity.ok().build();
    }

    @PostMapping("")
    public ResponseEntity<ShibbolethData> saveShibbolethData(@RequestBody ShibbolethData shibbolethData) {
        return ResponseEntity.ok(this.shibbolethDataService.save(shibbolethData));
    }
}

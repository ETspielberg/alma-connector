package org.unidue.ub.libintel.almaconnector.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.unidue.ub.libintel.almaconnector.model.openaccess.ApcStatistics;
import org.unidue.ub.libintel.almaconnector.model.openaccess.JournalApcDataDto;
import org.unidue.ub.libintel.almaconnector.service.ApcDataService;

import java.util.List;

@Controller
@RequestMapping("/openaccess/api/v1/")
public class OpenAccessRestController {

    private final ApcDataService apcDataService;

    public OpenAccessRestController(ApcDataService apcDataService) {
        this.apcDataService = apcDataService;
    }


    // ------------------------------ Data controller ---------------------------------------------------------
    @GetMapping("acpStatistics")
    private ResponseEntity<List<ApcStatistics>> getAllApcData() {
        return ResponseEntity.ok(this.apcDataService.getAllApcStatistics());
    }

    @GetMapping("acpStatistics/{mmsId}")
    private ResponseEntity<ApcStatistics> getSingleApcData(@PathVariable String mmsId) {
        return ResponseEntity.ok(this.apcDataService.getApcStatistics(mmsId));
    }

    @PostMapping("acpStatistics")
    public ResponseEntity<ApcStatistics> saveApcData(@RequestBody ApcStatistics apcStatistics) {
        return ResponseEntity.ok(this.apcDataService.saveApcStatistics(apcStatistics));
    }

    // ------------------------------ journal apc data controller ---------------------------------------------
    @GetMapping(value = "journalApcData", produces = "application/json")
    private ResponseEntity<List<JournalApcDataDto>> getAllJournalApcData() {
        return ResponseEntity.ok(this.apcDataService.getAllJournalApcData());
    }

    @GetMapping(value = "journalApcData/{mmsId}", produces = "application/json")
    private ResponseEntity<JournalApcDataDto> getSingleJournalApcData(@PathVariable String mmsId) {
        return ResponseEntity.ok(new JournalApcDataDto(this.apcDataService.getApcStatistics(mmsId)));
    }

    @PostMapping(value = "journalApcData", produces ="application/json")
    public ResponseEntity<JournalApcDataDto> saveJournalApcData(@RequestBody JournalApcDataDto journalApcDataDto, @RequestParam String mode) {
        return ResponseEntity.ok(this.apcDataService.saveJournalApcData(journalApcDataDto, mode));
    }

    // ------------------------------ Control controller -------------------------------------------------------

    @PostMapping("updateApcStatistics")
    public ResponseEntity<?> updateApcStatistics() {
        this.apcDataService.saveApcReports();
        return ResponseEntity.ok("all apc data updated");
    }

}

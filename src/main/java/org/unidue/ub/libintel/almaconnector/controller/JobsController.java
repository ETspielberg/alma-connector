package org.unidue.ub.libintel.almaconnector.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.unidue.ub.libintel.almaconnector.model.jobs.JobIdWithDescription;
import org.unidue.ub.libintel.almaconnector.service.ScheduledService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaInvoiceService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaJobsService;

import java.util.List;


/**
 * provides endpoints to manually start jobs and scheduled services
 */
@Controller
@RequestMapping("/jobs")
public class JobsController {

    private final AlmaJobsService almaJobsService;

    private final ScheduledService scheduledService;

    private final AlmaInvoiceService almaInvoiceService;

    JobsController(AlmaJobsService almaJobsService,
                   ScheduledService scheduledService,
                   AlmaInvoiceService almaInvoiceService) {
        this.almaJobsService = almaJobsService;
        this.scheduledService = scheduledService;
        this.almaInvoiceService = almaInvoiceService;
    }

    @GetMapping("/updateList")
    public ResponseEntity<?> updateJobList() {
        this.almaJobsService.updateJobsList();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<JobIdWithDescription>> searchJobs(String term) {
        return ResponseEntity.ok(this.almaJobsService.searchJob(term));
    }

    @GetMapping("/updateStatisticsNotes")
    public ResponseEntity<?> runUpdateStatisticsNote() {
        this.scheduledService.updateStatisticField();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/updateStatistics")
    public ResponseEntity<?> updateStatistics() {
        this.scheduledService.updateStatisticField();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/updateEdiInvoices")
    public ResponseEntity<?> updateEdiInvoices(String vendorId) {
        this.almaInvoiceService.updateEdiInvoices(vendorId);
        return ResponseEntity.ok().build();
    }
}

package org.unidue.ub.libintel.almaconnector.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.unidue.ub.libintel.almaconnector.model.jobs.JobIdWithDescription;
import org.unidue.ub.libintel.almaconnector.service.ScheduledService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaJobsService;

import java.io.IOException;
import java.util.List;


@Controller
public class JobsController {

    private final AlmaJobsService almaJobsService;

    private final ScheduledService scheduledService;

    JobsController(AlmaJobsService almaJobsService,
                   ScheduledService scheduledService) {
        this.almaJobsService = almaJobsService;
        this.scheduledService = scheduledService;
    }

    @GetMapping("/jobs/updateList")
    public ResponseEntity<?> updateJobList() {
        this.almaJobsService.updateJobsList();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/jobs/search")
    public ResponseEntity<List<JobIdWithDescription>> searchJobs(String term) {
        return ResponseEntity.ok(this.almaJobsService.searchJob(term));
    }

    @GetMapping("/jobs/updateStatisticsNotes")
    public ResponseEntity<?> runUpdateStatisticsNote() throws IOException {
        this.scheduledService.updateStatisticField();
        return ResponseEntity.ok().build();
    }
}

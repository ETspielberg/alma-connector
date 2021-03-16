package org.unidue.ub.libintel.almaconnector.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.unidue.ub.libintel.almaconnector.model.jobs.JobIdWithDescription;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaJobsService;

import java.util.List;


@Controller
public class JobsController {

    private final AlmaJobsService almaJobsService;

    JobsController(AlmaJobsService almaJobsService) {
        this.almaJobsService = almaJobsService;
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
}

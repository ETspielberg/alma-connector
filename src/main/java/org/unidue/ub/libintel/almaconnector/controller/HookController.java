package org.unidue.ub.libintel.almaconnector.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.unidue.ub.alma.shared.bibs.HookUserRequest;
import org.unidue.ub.libintel.almaconnector.logging.JobLoggerService;
import org.unidue.ub.libintel.almaconnector.model.hook.*;
import org.unidue.ub.libintel.almaconnector.service.HookService;

@Controller
@RequestMapping("/hooks")
public class HookController {

    @Value("${libintel.alma.jobs.id.packaging:123456789}")
    private String packagingJobId;

    private final static Logger log = LoggerFactory.getLogger(HookController.class);

    private final HookService hookService;

    private final JobLoggerService jobLoggerService;

    public HookController(HookService hookService,
                          JobLoggerService jobLoggerService) {
        this.hookService = hookService;
        this.jobLoggerService = jobLoggerService;
    }

    @GetMapping("/jobListener")
    public ResponseEntity<Challenge> answerJobChallenge(String challenge) {
        return ResponseEntity.ok(new Challenge(challenge));
    }

    @PostMapping("/jobListener")
    public ResponseEntity<?> receiveJobHook(@RequestBody JobHook hookContent, @RequestHeader("X-Exl-Signature") String signature) {
        log.info(signature);
        this.jobLoggerService.logJob(hookContent.getJobInstance());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/itemListener")
    public ResponseEntity<Challenge> answerItemChallenge(String challenge) {
        return ResponseEntity.ok(new Challenge(challenge));
    }

    @PostMapping("/itemListener")
    public ResponseEntity<?> receiveItemHook(@RequestBody ItemHook hookContent, @RequestHeader("X-Exl-Signature") String signature) {
        log.info(signature);
        this.hookService.processItemHook(hookContent);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/bibListener")
    public ResponseEntity<Challenge> answerBibChallenge(String challenge) {
        return ResponseEntity.ok(new Challenge(challenge));
    }

    @PostMapping("/bibListener")
    public ResponseEntity<?> receiveBibHook(@RequestBody BibHook hookContent, @RequestHeader("X-Exl-Signature") String signature) {
        log.info(signature);
        log.info(hookContent.getBib().toString());
        this.hookService.processBibHook(hookContent);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/requestsListener")
    public ResponseEntity<Challenge> answerRequestChallenge(String challenge) {
        return ResponseEntity.ok(new Challenge(challenge));
    }

    @PostMapping("/requestsListener")
    public ResponseEntity<?> receiveRequestHook(@RequestBody RequestHook hookContent, @RequestHeader("X-Exl-Signature") String signature) {
        log.info(signature);
        log.info(String.format("revceived hook of type %s", hookContent.getAction()));
        this.hookService.processRequestHook(hookContent);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/loanListener")
    public ResponseEntity<Challenge> answerLoanChallenge(String challenge) {
        return ResponseEntity.ok(new Challenge(challenge));
    }

    @PostMapping("/loanListener")
    public ResponseEntity<?> receiveLoanHook(@RequestBody LoanHook hookContent, @RequestHeader("X-Exl-Signature") String signature) {
        log.info(String.format("revceived hook of type %s", hookContent.getAction()));
        this.hookService.processLoanHook(hookContent);
        return ResponseEntity.ok().build();
    }
}

package org.unidue.ub.libintel.almaconnector.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.unidue.ub.libintel.almaconnector.model.hook.Challenge;
import org.unidue.ub.libintel.almaconnector.model.hook.LoanHook;
import org.unidue.ub.libintel.almaconnector.service.HookService;

@Controller
@RequestMapping("/hooks")
public class HookController {

    @Value("${libintel.alma.jobs.id.packaging:123456789}")
    private String packagingJobId;

    private final static Logger log = LoggerFactory.getLogger(HookController.class);

    private final HookService hookService;

    public HookController(HookService hookService) {
        this.hookService = hookService;
    }

    @GetMapping("/jobListener")
    public ResponseEntity<Challenge> answerJobChallenge(String challenge) {
        return ResponseEntity.ok(new Challenge(challenge));
    }

    @PostMapping("/jobListener")
    public ResponseEntity<?> receiveJobHook(@RequestBody String hookContent, @RequestHeader("X-Exl-Signature") String signature) {
        log.info(signature);
        log.info(hookContent);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/requestsListener")
    public ResponseEntity<Challenge> answerRequestChallenge(String challenge) {
        return ResponseEntity.ok(new Challenge(challenge));
    }

    @PostMapping("/requestsListener")
    public ResponseEntity<?> receiveRequestHook(@RequestBody String hookContent, @RequestHeader("X-Exl-Signature") String signature) {
        log.info(signature);
        log.info(hookContent);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/loanListener")
    public ResponseEntity<Challenge> answerLoanChallenge(String challenge) {
        return ResponseEntity.ok(new Challenge(challenge));
    }

    @PostMapping("/loanListener")
    public ResponseEntity<?> receiveLoanHook(@RequestBody LoanHook hookContent, @RequestHeader("X-Exl-Signature") String signature) {
        log.info(signature);
        this.hookService.processLoanHook(hookContent);
        return ResponseEntity.ok().build();
    }
}

package org.unidue.ub.libintel.almaconnector.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.unidue.ub.libintel.almaconnector.logging.JobLoggerService;
import org.unidue.ub.libintel.almaconnector.model.hook.*;
import org.unidue.ub.libintel.almaconnector.service.HookService;
import org.unidue.ub.libintel.almaconnector.service.alma.HookValidatorService;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@Controller
@RequestMapping("/hooks")
@Slf4j
public class HookController {

    @Value("${libintel.alma.jobs.id.packaging:123456789}")
    private String packagingJobId;

    private final HookService hookService;

    private final JobLoggerService jobLoggerService;

    private final HookValidatorService hookValidatorService;

    public HookController(HookService hookService,
                          JobLoggerService jobLoggerService,
                          HookValidatorService hookValidatorService) {
        this.hookService = hookService;
        this.jobLoggerService = jobLoggerService;
        this.hookValidatorService = hookValidatorService;
    }

    @GetMapping("/userListener")
    public ResponseEntity<Challenge> answerUserChallenge(String challenge) {
        return ResponseEntity.ok(new Challenge(challenge));
    }

    @PostMapping("/userListener")
    public ResponseEntity<?> receiveUserHook(@RequestBody UserHook hookContent, @RequestHeader("X-Exl-Signature") String signature) {
        log.info(String.format("revceived hook of type %s", hookContent.getAction()));
        return ResponseEntity.ok().build();
    }


    @GetMapping("/jobListener")
    public ResponseEntity<Challenge> answerJobChallenge(String challenge) {
        return ResponseEntity.ok(new Challenge(challenge));
    }

    @PostMapping("/jobListener")
    public ResponseEntity<?> receiveJobHook(@RequestBody JobHook hookContent, @RequestHeader("X-Exl-Signature") String signature) {
        log.info(String.format("revceived hook of type %s", hookContent.getAction()));
        this.jobLoggerService.logJob(hookContent.getJobInstance());
        return ResponseEntity.ok().build();
    }


    @GetMapping("/itemListener")
    public ResponseEntity<Challenge> answerItemChallenge(String challenge) {
        return ResponseEntity.ok(new Challenge(challenge));
    }

    @PostMapping("/itemListener")
    public ResponseEntity<?> receiveItemHook(@RequestBody ItemHook hookContent, @RequestHeader("X-Exl-Signature") String signature) {
        log.info(String.format("revceived hook of type %s", hookContent.getAction()));
        this.hookService.processItemHook(hookContent);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/bibListener")
    public ResponseEntity<Challenge> answerBibChallenge(String challenge) {
        return ResponseEntity.ok(new Challenge(challenge));
    }

    @PostMapping("/bibListener")
    public ResponseEntity<?> receiveBibHook(@RequestBody BibHook hookContent, @RequestHeader("X-Exl-Signature") String signature) {
        log.info(String.format("revceived hook of type %s", hookContent.getAction()));
        this.hookService.processBibHook(hookContent);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/requestsListener")
    public ResponseEntity<Challenge> answerRequestChallenge(String challenge) {
        return ResponseEntity.ok(new Challenge(challenge));
    }

    @PostMapping("/requestsListener")
    public ResponseEntity<?> receiveRequestHook(@RequestBody RequestHook hookContent, @RequestHeader("X-Exl-Signature") String signature) {
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

    @GetMapping("/listener/{hookType}")
    public ResponseEntity<Challenge> answerChallenge(String challenge, @PathVariable String hookType) {
        return ResponseEntity.ok(new Challenge(challenge));
    }

    @PostMapping("/listener/{hookType}")
    public ResponseEntity<?> receiveLoan(@PathVariable String hookType, @RequestBody String hookContent, @RequestHeader("X-Exl-Signature") String signature) throws NoSuchAlgorithmException, NoSuchProviderException, JsonProcessingException, InvalidKeyException {
        if (this.hookValidatorService.isValid(hookContent, signature)) {
            this.hookService.processHook(hookContent, hookType);
            return ResponseEntity.ok().build();
        } else
            return ResponseEntity.badRequest().build();
    }
}

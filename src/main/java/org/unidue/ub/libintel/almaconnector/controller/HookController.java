package org.unidue.ub.libintel.almaconnector.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.unidue.ub.alma.shared.conf.JobInstance;
import org.unidue.ub.libintel.almaconnector.model.hook.Challenge;

@Controller
@RequestMapping("/hooks")
public class HookController {

    @Value("${libintel.alma.jobs.id.packaging}")
    private String packagingJobId;

    private final static Logger log = LoggerFactory.getLogger(HookController.class);

    @GetMapping("/jobListener")
    public ResponseEntity<Challenge> answerChallenge(String challenge) {
        return ResponseEntity.ok(new Challenge(challenge));
    }

    @PostMapping("/jobListener")
    public ResponseEntity<?> receiveHook(@RequestBody String hookContent, @RequestHeader("X-Exl-Signature") String signature) {
        log.info(signature);
        log.info(hookContent);
        return ResponseEntity.ok().build();
    }
}

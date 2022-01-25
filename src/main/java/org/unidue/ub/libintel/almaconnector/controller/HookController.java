package org.unidue.ub.libintel.almaconnector.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.unidue.ub.libintel.almaconnector.model.hook.*;
import org.unidue.ub.libintel.almaconnector.service.RedisService;
import org.unidue.ub.libintel.almaconnector.service.alma.HookValidatorService;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * provides endpoints to handle webhooks sent from alma. Each endpoint consists of a GET endpoint with a challenge
 * parameter, which is returned as Challenge object, and a POST endpoint receiving one of the following webhooks
 *  - /userListener handles changes in alma users
 *  - /jobListener handles messages from finished jobs
 *  - /loanListener handles messages from loan events
 *  - /requestListener handles messages from request events
 *  - /itemListener handles messages for item changes like creation or modification
 *  - /bibListener handles messages for bib record changes like creation or modification
 */
@Controller
@RequestMapping("/hooks")
@Slf4j
public class HookController {

    private final HookValidatorService hookValidatorService;

    private final RedisService redisService;


    public HookController(HookValidatorService hookValidatorService,
                          RedisService redisService) {
        this.redisService = redisService;
        this.hookValidatorService = hookValidatorService;
    }

    @GetMapping("/listener/{hookType}")
    public ResponseEntity<Challenge> answerChallenge(String challenge, @PathVariable String hookType) {
        log.debug(String.format("challenging %s hook endpoint.", hookType));
        return ResponseEntity.ok(new Challenge(challenge));
    }

    @PostMapping("/listener/{hookType}")
    public ResponseEntity<?> receiveLoan(@PathVariable String hookType, @RequestBody String hookContent, @RequestHeader("X-Exl-Signature") String signature) throws NoSuchAlgorithmException, InvalidKeyException {
        if (this.hookValidatorService.isValid(hookContent, signature)) {
            log.debug("Hook passed validation");
            this.redisService.cacheHook(hookContent, hookType);
            return ResponseEntity.ok().build();
        } else {
            log.warn("hook did not pass validation");
            return ResponseEntity.badRequest().build();
        }
    }
}

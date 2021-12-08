package org.unidue.ub.libintel.almaconnector.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.almaconnector.model.hook.*;
import org.unidue.ub.libintel.almaconnector.service.HookService;
import org.unidue.ub.libintel.almaconnector.service.RedisService;

@Slf4j
@Service
public class RedisListener implements MessageListener {

    private final RedisService redisService;

    private final HookService hookService;

    RedisListener(RedisService redisService,
                  HookService hookService) {
        this.redisService = redisService;
        this.hookService = hookService;
    }

    @Override
    public void onMessage(Message message, byte[] bytes) {
        String body = new String(message.getBody());
        String[] parts = body.split(":");
        if (parts.length == 2) {
            if (body.contains("job_hook")) {
                JobHook jobHook = this.redisService.getExpiredJobHook(parts[1]);
                this.hookService.processJobHook(jobHook);
            } else if (body.contains("item_hook")) {
                ItemHook itemHook = this.redisService.getExpiredItemHook(parts[1]);
                this.hookService.processItemHook(itemHook);
            } else if (body.contains("bib_hook")) {
                BibHook bibHook = this.redisService.getExpiredBibHook(parts[1]);
                this.hookService.processBibHook(bibHook);
            } else if (body.contains("loan_hook")) {
                LoanHook loanHook = this.redisService.getExpiredLoanHook(parts[1]);
                this.hookService.processLoanHook(loanHook);
            } else if (body.contains("request_hook")) {
                RequestHook requestHook = this.redisService.getExpiredRequestHook(parts[1]);
                this.hookService.processRequestHook(requestHook);
            } else if (body.contains("user_hook")) {
                UserHook userHook = this.redisService.getExpiredUserHook(parts[1]);
                this.hookService.processUserHook(userHook);
            }
        }
    }
}

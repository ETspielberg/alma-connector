package org.unidue.ub.libintel.almaconnector.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.almaconnector.clients.alma.analytics.AnalyticsNotRetrievedException;

@Service
@Slf4j
public class LogService {

    private final MailSenderService mailSenderService;

    LogService(MailSenderService mailSenderService) {
        this.mailSenderService = mailSenderService;
    }

    public void handleAnalyticsException(AnalyticsNotRetrievedException analyticsNotRetrievedException) {
        mailSenderService.sendAlertMail(analyticsNotRetrievedException);
        log.warn("could not retrieve analytics report.", analyticsNotRetrievedException);
    }
}

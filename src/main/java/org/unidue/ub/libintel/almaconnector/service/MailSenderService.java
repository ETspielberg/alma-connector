package org.unidue.ub.libintel.almaconnector.service;

import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.Item;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * handles the sending of mails
 */

@Slf4j
@Service
public class MailSenderService {

    // the email address to appear as "from"
    @Value("${libintel.regalfinder.email.from:no-reply@uni-due.de}")
    private String regalfinderMailFrom;

    // the list of recipients for the regalfinder mail
    @Value("${libintel.regalfinder.email.to}")
    private String[] regalfinderMailTo;

    // the list of recipients for the regalfinder mail
    @Value("${libintel.admins.mail}")
    private String[] libintelAdmins;

    private final JavaMailSender emailSender;

    private final TemplateEngine templateEngine;

    private final static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");


    /**
     * constructor based autowiring of email sender and the appropriate mail creation services.
     *
     * @param emailSender    the mail sender service as instantiated from the config properties
     * @param templateEngine the thymeleaf templating engine
     */
    MailSenderService(JavaMailSender emailSender, TemplateEngine templateEngine) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * sends the mail, if an item is not found in the regalfinder
     *
     * @param item the item which is not found by the regalfinder
     */
    public void sendNotificationMail(Item item) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(regalfinderMailFrom);
            messageHelper.setTo(regalfinderMailTo);
            messageHelper.setSubject("Regalfinder | neues Exemplar wird nicht gefunden");
            messageHelper.setText(buildRegalfinderMail(item), true);
        };
        emailSender.send(messagePreparator);
    }

    /**
     * sends the mail, if an item is not found in the regalfinder
     *
     * @param exception the exception which occured
     */
    public void sendAlertMail(Exception exception) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(regalfinderMailFrom);
            messageHelper.setTo(libintelAdmins);
            messageHelper.setSubject("Ein Fehler ist aufgetreten!");
            messageHelper.setText(buildExceptionMail(exception), true);
        };
        emailSender.send(messagePreparator);
    }

    private String buildRegalfinderMail(Item item) {
        Context context = new Context();
        context.setVariable("collection", item.getItemData().getLocation().getValue());
        context.setVariable("shelfmark", item.getItemData().getAlternativeCallNumber());
        context.setVariable("mmsId", item.getBibData().getMmsId());
        context.setVariable("title", item.getBibData().getTitle());
        context.setVariable("author", item.getBibData().getAuthor());
        return templateEngine.process("regalfinderNotificationMailTemplate", context);
    }

    private String buildExceptionMail(Exception exception) {
        Context context = new Context();
        context.setVariable("timestamp", dateformat.format(new Date()));
        context.setVariable("message", exception.getMessage());
        context.setVariable("stacktrace", exception.getStackTrace());
        context.setVariable("cause", exception.getCause());
        context.setVariable("class", exception.getClass());
        context.setVariable("localized", exception.getLocalizedMessage());
        return templateEngine.process("errorMail", context);
    }
}
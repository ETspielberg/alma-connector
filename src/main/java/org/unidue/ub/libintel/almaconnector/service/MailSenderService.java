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

/**
 * handles the sending of mails
 */

@Slf4j
@Service
public class MailSenderService {

    // the email address to appear as "from"
    @Value("${libintel.regalfinder.email.from:no-reply@uni-due.de}")
    private String regalfinderMailFrom;

    // the email address to appear as "from"
    @Value("${libintel.regalfinder.email.to}")
    private String[] regalfinderMailTo;

    private final JavaMailSender emailSender;

    private final TemplateEngine templateEngine;


    /**
     * constructor based autowiring of email sender and the appropriate mail creation services.
     *
     * @param emailSender                 the mail sender service as instantiated from the config properties
     * @param templateEngine              the thymeleaf templating engine
     */
    MailSenderService(JavaMailSender emailSender, TemplateEngine templateEngine) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * sends the mail, if an ebook is requested
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

    private String buildRegalfinderMail(Item item) {
        Context context = new Context();
        context.setVariable("collection", item.getItemData().getLibrary().getValue());
        context.setVariable("shelfmark", item.getItemData().getAlternativeCallNumber());
        context.setVariable("mmsId", item.getBibData().getMmsId());
        context.setVariable("title", item.getBibData().getTitle());
        context.setVariable("author", item.getBibData().getAuthor());
        return templateEngine.process("regalfinderNotificationMailTemplate", context);
        }
}
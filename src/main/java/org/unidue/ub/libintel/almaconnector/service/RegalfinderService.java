package org.unidue.ub.libintel.almaconnector.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.Item;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class RegalfinderService {

    private final MailSenderService mailSenderService;

    private final static String Regalfinder_URL = "https://services.ub.uni-due.de/ub-map/regalfinder.html?standort=%s&signatur=%s&lang=de&XSL.Style=xml";

    public RegalfinderService(MailSenderService mailSenderService) {
        this.mailSenderService = mailSenderService;
    }

    @Cacheable("regalfinder")
    public boolean checkRegalfinder(String collection, String shelfmark) throws IOException {
        // do not check resource sharing items
        if ("RES_SHARE".equals(collection))
            return true;

        String resourceUrl = String.format(Regalfinder_URL, URLEncoder.encode(collection, StandardCharsets.UTF_8), URLEncoder.encode(shelfmark, StandardCharsets.UTF_8));
        return Jsoup.connect(resourceUrl).get().select("regal").size() > 0;
    }

    public void checkRegalfinder(Item item) {
        try {
            boolean isInRegalfinder = this.checkRegalfinder(item.getItemData().getLocation().getValue(), item.getItemData().getAlternativeCallNumber());
            if (!isInRegalfinder) {
                this.mailSenderService.sendNotificationMail(item);
                log.warn(String.format("item is not in regalfinder: %s, %s %s", item.getBibData().getMmsId(), item.getItemData().getPid(), item.getItemData().getAlternativeCallNumber()));
            }
        } catch (IOException ioe) {
            log.warn("could not check regalfinder", ioe);
        }
    }
}

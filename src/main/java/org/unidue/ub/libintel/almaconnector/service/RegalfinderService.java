package org.unidue.ub.libintel.almaconnector.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.Item;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class RegalfinderService {

    private final MailSenderService mailSenderService;

    private final List<String> ignoredLocations = Arrays.asList("ENP", "DNP", "EPR", "DPR", "DBB", "EBB", "AFL", "DES", "EES", "DHS", "UNASSIGNED", "FL-AUSL", "FL-LS"," ELS", "ERK", "DRK","EHS", "EHA", "MPR", "MNP", "MHS", "E08", "E36", "E37", "E60", "E70", "E84", "E92", "E94", "E96", "E98", "D04", "D06", "D07", "D08", "D09", "D16", "D50", "D92", "D94", "D96", "D98");

    private final static String Regalfinder_URL = "https://services.ub.uni-due.de/ub-map/regalfinder.html?standort=%s&signatur=%s&lang=de&XSL.Style=xml";

    public RegalfinderService(MailSenderService mailSenderService) {
        this.mailSenderService = mailSenderService;
    }

    /**
     * checks whether the item (collection and shelfmark) is displayed in the regalfinder
     * @param collection the collection of the item to be checked
     * @param shelfmark the shelfmark of the item to be checked
     * @return true, if the response from the regalfinder contains the node 'regal'
     * @throws IOException thrown, if an error occurred upon connecting to the regalfinder web application
     */
    @Cacheable("regalfinder")
    public boolean checkRegalfinder(String collection, String shelfmark) throws IOException {
        String resourceUrl = String.format(Regalfinder_URL, URLEncoder.encode(collection, StandardCharsets.UTF_8), URLEncoder.encode(shelfmark, StandardCharsets.UTF_8));
        return Jsoup.connect(resourceUrl).get().select("regal").size() > 0;
    }

    /**
     * checks whether the item is displayed in the regalfinder
     * @param item the item to be checked
     */
    public void checkRegalfinder(Item item) {
        // if it is an item from the resource sharing library, do nothing
        if ("RES_SHARE".equals(item.getItemData().getLibrary().getValue()))
            return;

        // if it is a key to a locker, do nothing
        if ("KEY".equals(item.getItemData().getPhysicalMaterialType().getValue()))
            return;

        // check shelfmark. if none is given, the shelfmark is too short (normally only notation from elisa),
        // or it is a magazin shelfmark, do nothing.
        String shelfmark = item.getItemData().getAlternativeCallNumber();
        if ( shelfmark == null || shelfmark.length() < 5 || shelfmark.startsWith("ZZ"))
            return;
        if (!isShelfmark(shelfmark.strip()))
            return;

        // check the location. if it is none or a none-publishing location, do nothing
        String location = item.getItemData().getLocation().getValue();
        if (location == null || ignoredLocations.contains(location))
            return;
        try {
            boolean isInRegalfinder = this.checkRegalfinder(location, shelfmark);
            if (!isInRegalfinder) {
                this.mailSenderService.sendNotificationMail(item);
                log.warn(String.format("item is not in regalfinder: %s, %s %s", item.getBibData().getMmsId(), item.getItemData().getPid(), item.getItemData().getAlternativeCallNumber()));
            }
        } catch (IOException ioe) {
            log.warn("could not check regalfinder", ioe);
        }
    }

    public static boolean isShelfmark (String s){
        Pattern pattern = Pattern.compile("^[A-Za]{3,4}\\d+");
        Matcher matcher = pattern.matcher(s);
        return matcher.find();
    }
}

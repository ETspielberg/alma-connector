package org.unidue.ub.libintel.almaconnector.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class RegalfinderService {

    private final static String Regalfinder_URL = "https://services.ub.uni-due.de/ub-map/regalfinder.html?standort=%s&signatur=%s&lang=de&XSL.Style=xml";

    @Cacheable("regalfinder")
    public boolean checkRegalfinder(String collection, String shelfmark) throws IOException {
        String resourceUrl = String.format(Regalfinder_URL, URLEncoder.encode(collection, StandardCharsets.UTF_8), URLEncoder.encode(shelfmark, StandardCharsets.UTF_8));
        return Jsoup.connect(resourceUrl).get().select("regal").size() > 0;
    }
}

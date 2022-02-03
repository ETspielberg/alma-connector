package org.unidue.ub.libintel.almaconnector.service;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.unidue.ub.libintel.almaconnector.model.bubi.dto.AlmaItemData;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * offers functions to retrieve item data from primo
 *
 * @author Eike Spielberg
 * @author eike.spielberg@uni-due.de
 * @version 1.0
 */
@Service
@Slf4j
public class PrimoService {

    @Value("${libintel.primo.vid}")
    String primoVid;

    @Value("${libintel.alma.api.key.general}")
    private String primoApiKey;

    private final static String PRIMO_BASE_URL = "https://api-eu.hosted.exlibrisgroup.com/primo/v1/search?q=%s&vid=%s&apikey=%s";

    /**
     * retrieves alma data about a given signature
     * @param almaItemData the alma item data provided
     * @return the item data extended by the primo response
     */
    public List<AlmaItemData> getPrimoResponse(AlmaItemData almaItemData) {
        try {
            // determine media type
            almaItemData.mediaType = "book";
            if (almaItemData.shelfmark.contains(" Z "))
                almaItemData.mediaType = "journal";

            // execute query
            String response = getResponseForJson(almaItemData.shelfmark);
            List<AlmaItemData> foundJournals = new ArrayList<>();
            if (!"".equals(response)) {
                log.debug(String.format("checking for %s: %s", almaItemData.collection, almaItemData.shelfmark));
                //log.debug(response);
                DocumentContext jsonContext = JsonPath.parse(response);
                List<Object> documents = jsonContext.read("$['docs'][*]");
                log.debug("found " + documents.size() + " documents");
                for (int i = 0; i < documents.size(); i++) {
                    log.debug("processing document " + i);
                    String basePath = "$['docs'][" + i + "]";

                    try {
                        log.debug(jsonContext.read(basePath + "['pnx']['display']['type'][0]"));
                        if (!almaItemData.mediaType.equals(jsonContext.read(basePath + "['pnx']['display']['type'][0]"))) {
                            log.debug("wrong media type. skipping document.");
                            continue;
                        }
                        String title = jsonContext.read(basePath + "['pnx']['display']['title'][0]");
                        almaItemData.title = title;
                        if (almaItemData.collection.isEmpty()) {
                            // building list of AlmaItemData from shelfmark
                            log.debug("no collection set. retrieving collections from primo.");
                            List<Object> collections = jsonContext.read(basePath + "['delivery']['holding']");
                            log.debug(String.format("found %d holdings in primo", collections.size()));
                            for (int j = 0; j < collections.size(); j++) {
                                String holdingPath = "$['docs'][" + i + "]['delivery']['holding'][" + j + "]";
                                log.debug("reading collection from path: " + holdingPath + "['subLocationCode']");
                                String collection = "";
                                try {
                                    collection = jsonContext.read(holdingPath + "['subLocationCode']");
                                    log.debug("read collction " + collection);
                                } catch (PathNotFoundException collectionPathException) {
                                    log.warn("could not read shelfmark from response", collectionPathException);
                                    continue;
                                }
                                if (collection.startsWith("D")) {
                                    log.debug("skip duisburg entry");
                                    continue;
                                }

                                String shelfmark = "";
                                log.debug("reading shelfmark from path: " + holdingPath + "['callNumber']");
                                try {
                                    shelfmark = jsonContext.read(holdingPath + "['callNumber']");
                                    log.debug("read shelfmark " + shelfmark);
                                } catch (PathNotFoundException shelfmarkPathException) {
                                    log.warn("could not read shelfmark from response", shelfmarkPathException);
                                    continue;
                                }
                                if (!shelfmark.equals(almaItemData.shelfmark)) {
                                    log.debug("shelfmarks do not match. skipping entry.");
                                    continue;
                                }
                                String holdingId = jsonContext.read(holdingPath + "['holdId']");
                                String mmsId = jsonContext.read(holdingPath + "['ilsApiId']");
                                String campus = jsonContext.read(holdingPath + "['libraryCode']");
                                log.info(String.format("adding data for collection %s and shelfmark %s", collection, shelfmark));
                                AlmaItemData newJournalData = almaItemData.clone()
                                        .withHoldingId(holdingId)
                                        .withMmsId(mmsId)
                                        .withTitle(title)
                                        .withCampus(campus)
                                        .withCollection(collection);
                                foundJournals.add(newJournalData);
                            }
                        } else {
                            String location = jsonContext.read(basePath + "['delivery']['bestlocation']['subLocationCode']");
                            log.debug("found location: " + location);
                            if (!almaItemData.collection.equals(location)) {
                                log.debug("wrong location. skipping entry.");
                                continue;
                            }
                            String shelfmark = jsonContext.read(basePath + "['delivery']['bestlocation']['callNumber']");
                            log.debug("found shelfmark: " + shelfmark);
                            if (!shelfmark.contains(almaItemData.shelfmark)) {
                                log.debug("shelfmarks do not match. skipping entry");
                                continue;
                            }
                            String campus = jsonContext.read(basePath + "['delivery']['bestlocation']['libraryCode']");
                            log.debug("found campus: " + campus);
                            log.info("found match");
                            AlmaItemData newJournalData = almaItemData.clone()
                                    .withHoldingId(jsonContext.read(basePath + "['delivery']['bestlocation']['holdId']"))
                                    .withMmsId(jsonContext.read(basePath + "['delivery']['bestlocation']['ilsApiId']"))
                                    .withTitle(title)
                                    .withCampus(campus);
                            foundJournals.add(newJournalData);
                        }
                    } catch (PathNotFoundException pnfe) {
                        log.warn("no url given");
                    }
                }
            }
            log.info("found " + foundJournals.size() + " matches in Primo");
            return foundJournals;
        } catch (Exception e) {
            log.error("error occured", e);
            return new ArrayList<>();
        }
    }

    private String getResponseForJson(String shelfmark) {
        if (shelfmark.contains("#"))
            shelfmark = shelfmark.replace("#", "*");
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        shelfmark = URLEncoder.encode(shelfmark, StandardCharsets.UTF_8);
        String query = "holding_call_number,exact," + shelfmark;
        String resourceUrl = String.format(PRIMO_BASE_URL, query, primoVid, primoApiKey);
        log.info("querying Primo API with " + resourceUrl);
        ResponseEntity<String> response = restTemplate.getForEntity(resourceUrl, String.class);
        if (response.getStatusCode().equals(HttpStatus.OK))
            return response.getBody();
        else
            return "";
    }
}

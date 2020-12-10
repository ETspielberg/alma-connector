package org.unidue.ub.libintel.almaconnector.service;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.unidue.ub.libintel.almaconnector.model.bubi.AlmaJournalData;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class PrimoService {

    @Value("${primo.vid}")
    String primoVid;

    @Value("${alma.api.acq.key}")
    private String primoApiKey;

    private final static String PRIMO_BASE_URL = "https://api-eu.hosted.exlibrisgroup.com/primo/v1?q=%s&vid=%s";

    private final Logger log = LoggerFactory.getLogger(PrimoService.class);

    public List<AlmaJournalData> getPrimoResponse(AlmaJournalData almaJournalData) {
        String response = getResponseForJson(almaJournalData.shelfmark);
        List<AlmaJournalData> foundJournals = new ArrayList<>();
        if (!"".equals(response)) {
            DocumentContext jsonContext = JsonPath.parse(response);
            List<Object> documents = jsonContext.read("$['docs'][*]");
            log.debug("found " + documents.size() + " documents");
            int numberOfDocs = documents.size();
            for (int i = 0; i < numberOfDocs; i++) {
                String basePath = "$['docs'][" + i + "]";
                try {
                    String location = jsonContext.read(basePath + "['delivery']['bestlocation'][subLocationCode]");
                    if (!almaJournalData.collection.equals(location))
                        continue;
                    String shelfmark = jsonContext.read(basePath + "['delivery']['bestlocation'][callNumber]");
                    if (!shelfmark.contains(almaJournalData.shelfmark))
                        continue;
                    AlmaJournalData newJournalData = almaJournalData.clone()
                            .withHoldingId(jsonContext.read(basePath + "['delivery']['bestlocation'][holdId]"))
                            .withMmsId(jsonContext.read(basePath + "['delivery']['bestlocation'][ilsApiId]"));

                    foundJournals.add(newJournalData);
                } catch (PathNotFoundException pnfe) {
                    log.debug("no url given");
                }
            }
        }
        return foundJournals;
    }

    private String getResponseForJson(String shelfmark) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        String query = "holding_call_number,contains," + shelfmark;
        String resourceUrl = String.format(PRIMO_BASE_URL, query, primoApiKey);
        log.info("querying Primo API with " + resourceUrl);
        ResponseEntity<String> response = restTemplate.getForEntity(resourceUrl, String.class);
        if (response.getStatusCode().equals(HttpStatus.OK))
            return response.getBody();
        else
            return "";
    }
}

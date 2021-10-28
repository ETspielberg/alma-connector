package org.unidue.ub.libintel.almaconnector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.BibWithRecord;
import org.unidue.ub.alma.shared.bibs.Item;
import org.unidue.ub.alma.shared.user.AlmaUser;
import org.unidue.ub.libintel.almaconnector.model.hook.HookEventTypes;
import org.unidue.ub.libintel.almaconnector.model.hook.LoanHook;
import org.unidue.ub.libintel.almaconnector.model.hook.RequestHook;
import org.unidue.ub.libintel.almaconnector.model.media.Event;
import org.unidue.ub.libintel.almaconnector.model.media.Manifestation;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaCatalogService;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ElasticsearchService {

    private final RestHighLevelClient elasticsearchClient;

    private final AlmaCatalogService almaCatalogService;

    private final ObjectMapper objectMapper;

    @Value("${elasticsearch.manifestation.index:manifestation}")
    private String indexName;

    ElasticsearchService(RestHighLevelClient elasticsearchClient,
                         ObjectMapper objectMapper,
                         AlmaCatalogService almaCatalogService) {
        this.elasticsearchClient = elasticsearchClient;
        this.objectMapper = objectMapper;
        this.almaCatalogService = almaCatalogService;
    }

    public int index(Manifestation manifestation, String id) {
        Map<String, Object> documentMapper = objectMapper.convertValue(manifestation, Map.class);
        IndexRequest indexRequest = new IndexRequest(indexName, "_doc", id)
                .source(documentMapper);
        try {
            IndexResponse indexResponse = elasticsearchClient.index(indexRequest, RequestOptions.DEFAULT);
            log.info(String.format("index request returned status %d", indexResponse.status().getStatus()));
            log.info("indexed new document " + id);
            return indexResponse.status().getStatus();
        } catch (IOException ioe) {
            log.warn("Problem indexing manifestation, ", ioe);
        }
        return 0;
    }

    public int index(Item almaItem, Date inventoryDate) {
        String mmsId = almaItem.getBibData().getMmsId();
        org.unidue.ub.libintel.almaconnector.model.media.Item item = new org.unidue.ub.libintel.almaconnector.model.media.Item(almaItem, inventoryDate);
        Manifestation manifestation = retrieveManifestation(mmsId);
        if (manifestation == null)
            manifestation = findManifestationByMmsId(mmsId);
        if (manifestation == null) {
            BibWithRecord bib = this.almaCatalogService.getRecord(almaItem.getBibData().getMmsId());
            if (bib == null) {
                log.error("no record available for mms id " + mmsId);
                return 0;
            }
            manifestation = new Manifestation(bib);
        }
        manifestation.addItem(item);
        int response = index(manifestation, mmsId);
        log.info(String.format("index request returned status %d", response));
        log.info(String.format("indexed new item %s to document %s ", almaItem.getItemData().getPid(), mmsId));
        return response;
    }

    public void updateManifestation(Manifestation manifestation) {
        Map<String, Object> documentMapper = objectMapper.convertValue(manifestation, Map.class);
        UpdateRequest updateRequest = new UpdateRequest(indexName, "_doc", manifestation.getTitleID()).upsert(documentMapper);
        try {
            elasticsearchClient.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException ioe) {
            log.warn("error on trying to updating entry " + manifestation.getTitleID(), ioe);
        }
    }

    private Manifestation findManifestationByMmsId(String mmsId) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(0);
        sourceBuilder.size(5);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        sourceBuilder.query(QueryBuilders.termQuery("mmsId", mmsId));
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse;
        try {
            searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException ioe) {
            log.warn("Problem indexing manifestation, ", ioe);
            return null;
        }
        SearchHits hits = searchResponse.getHits();
        if (hits.totalHits == 1) {
            SearchHit searchHit = Arrays.stream(hits.getHits()).findFirst().orElse(null);
            if (searchHit == null)
                return null;
            return retrieveManifestation(searchHit.getId());
        } else
            return null;
    }

    public Manifestation retrieveManifestation(String id) {
        GetRequest getRequest = new GetRequest(indexName, "_doc", id);
        try {
            final GetResponse response = elasticsearchClient.get(getRequest, RequestOptions.DEFAULT);
            return objectMapper.readValue(response.getSourceAsBytes(), Manifestation.class);
        } catch (IOException ioe) {
            log.warn("Problem indexing manifestation, ", ioe);
            return null;
        }
    }

    public void deleteItem(Item almaItem, Date date) {
        String mmsId = almaItem.getBibData().getMmsId();
        Manifestation manifestation = retrieveManifestation(mmsId);
        org.unidue.ub.libintel.almaconnector.model.media.Item item = manifestation.getItem(almaItem.getItemData().getPid());
        item.delete(date);
        this.updateManifestation(manifestation);
    }

    public void updateItem(Item almaItem, Date updateDate) {
        String mmsId = almaItem.getBibData().getMmsId();
        Manifestation manifestation = retrieveManifestation(mmsId);
        org.unidue.ub.libintel.almaconnector.model.media.Item item = manifestation.findCorrespindingItem(almaItem);
        if (item == null) {
            this.index(almaItem, updateDate);
        }
        else {
            item.update(almaItem);
            this.updateManifestation(manifestation);
        }
    }

    public void indexRequest(RequestHook hook, Item almaItem) {
        String eventType = hook.getEvent().getValue();
        String mmsId = hook.getUserRequest().getMmsId();
        Manifestation manifestation = retrieveOrBuildManifestation(mmsId, almaItem, hook.getTime());
        if (manifestation == null)
            return;
        org.unidue.ub.libintel.almaconnector.model.media.Item item = manifestation.getItem(almaItem.getItemData().getPid());
        item.addEvent(new Event(item, hook.getTime(), eventType, "", getDelta(eventType)));
        this.updateManifestation(manifestation);
    }

    public void indexLoan(LoanHook hook, Item almaItem, AlmaUser user) {
        String eventType = hook.getEvent().getValue();
        String mmsId = hook.getItemLoan().getMmsId();
        Manifestation manifestation = retrieveOrBuildManifestation(mmsId, almaItem, hook.getTime());
        if (manifestation == null)
            return;
        org.unidue.ub.libintel.almaconnector.model.media.Item item = manifestation.getItem(almaItem.getItemData().getPid());
        Event event = new Event(item, hook.getTime(), eventType, user.getUserGroup().getValue() , getDelta(eventType));
        if (HookEventTypes.LOAN_CREATED.name().equals(eventType))
            item.addEvent(event);
        else if (HookEventTypes.LOAN_RETURNED.name().equals(eventType))
            item.closeLoan(event);
        this.updateManifestation(manifestation);
    }

    private Manifestation retrieveOrBuildManifestation(String mmsId, Item almaItem, Date updateDate) {
        Manifestation manifestation = findManifestationByMmsId(mmsId);
        if (manifestation == null) {
            BibWithRecord bib = this.almaCatalogService.getRecord(mmsId);
            if (bib == null) {
                log.error("no record available for mms id " + mmsId);
                return null;
            }
            manifestation = new Manifestation(bib);
        }
        org.unidue.ub.libintel.almaconnector.model.media.Item item = manifestation.findCorrespindingItem(almaItem);
        if (item == null) {
            item = new org.unidue.ub.libintel.almaconnector.model.media.Item(almaItem, updateDate);
            manifestation.addItem(item);
            this.index(manifestation, mmsId);
        }
        return manifestation;
    }

    private int getDelta(String eventType) {
        switch (eventType) {
            case "BIB_CREATED":
            case "ITEM_CREATED":
            case "LOAN_CREATED":
            case "REQUEST_CREATED":
                return 1;
            case "BIB_DELETED":
            case "ITEM_DELETED":
            case "REQUEST_CLOSED":
            case "LOAN_RETURNED":
                return -1;
            default:
                return 0;
        }
    }


}

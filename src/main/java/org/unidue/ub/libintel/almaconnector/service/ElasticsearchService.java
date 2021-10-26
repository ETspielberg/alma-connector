package org.unidue.ub.libintel.almaconnector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
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
import org.unidue.ub.libintel.almaconnector.model.media.BibliographicInformation;
import org.unidue.ub.libintel.almaconnector.model.media.Manifestation;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ElasticsearchService {

    private final RestHighLevelClient elasticsearchClient;

    private final ObjectMapper objectMapper;

    @Value("${elasticsearch.manifestation.index:manifestation}")
    private String indexName;

    ElasticsearchService(RestHighLevelClient elasticsearchClient, ObjectMapper objectMapper) {
        this.elasticsearchClient = elasticsearchClient;
        this.objectMapper = objectMapper;
    }

    public int index(BibWithRecord bib) {
        Manifestation manifestation = new Manifestation(bib.getMmsId());
        manifestation.setBibliographicInformation(new BibliographicInformation(bib));
        return index(manifestation, bib.getMmsId());
    }

    public int index(Manifestation manifestation, String id) {
        Map<String, Object> documentMapper = objectMapper.convertValue(manifestation, Map.class);
        IndexRequest indexRequest = new IndexRequest(indexName, "_doc", id)
                .source(documentMapper);
        try {
            IndexResponse indexResponse = elasticsearchClient.index(indexRequest, RequestOptions.DEFAULT);
            log.info(String.format("index request returned status %d", indexResponse.status().getStatus()));
            return indexResponse.status().getStatus();
        } catch (IOException ioe) {
            log.warn("Problem indexing manifestation, ", ioe);
        }
        return 0;
    }

    public int index(Item almaItem) {
        String mmsId = almaItem.getBibData().getMmsId();
        org.unidue.ub.libintel.almaconnector.model.media.Item item = new org.unidue.ub.libintel.almaconnector.model.media.Item(almaItem);
        Manifestation manifestation = retrieveManifestation(mmsId);
        if (manifestation == null)
            manifestation = findManifestationByMmsId(mmsId);
        if (manifestation == null)
            return 0;
        manifestation.addItem(item);
        return index(manifestation, mmsId);
    }

    private Manifestation findManifestationByMmsId(String mmsId) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(0);
        sourceBuilder.size(5);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        sourceBuilder.query(QueryBuilders.termQuery("mmsId", mmsId));
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("posts");
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = null;
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

}

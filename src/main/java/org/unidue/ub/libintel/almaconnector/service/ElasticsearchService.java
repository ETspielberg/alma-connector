package org.unidue.ub.libintel.almaconnector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.BibWithRecord;

import java.io.IOException;
import java.util.Map;

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

    public int index(BibWithRecord bib) throws IOException {

        Map<String, Object> documentMapper = objectMapper.convertValue(bib, Map.class);

        IndexRequest indexRequest = new IndexRequest(indexName, "_doc", bib.getMmsId())
                .source(documentMapper);

        IndexResponse indexResponse = elasticsearchClient.index(indexRequest, RequestOptions.DEFAULT);
        log.info(String.format("index request returned status %d", indexResponse.status().getStatus()));
        return indexResponse.status().getStatus();
    }

}

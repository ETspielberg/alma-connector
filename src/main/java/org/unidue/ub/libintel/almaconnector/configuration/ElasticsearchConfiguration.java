package org.unidue.ub.libintel.almaconnector.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

@Slf4j
@Configuration
public class ElasticsearchConfiguration extends AbstractElasticsearchConfiguration {

    @Value("${elasticsearch.host:localhost}")
    private String elasticsearchHost;

    @Value("${elasticsearch.port:9200}")
    private int elasticsearchPort;

    @Value("${elasticsearch.protocol:http}")
    private String elasticsearchProtocol;

    @Bean
    public RestHighLevelClient elasticsearchClient() {
        log.debug(String.format("connecting via %s to elasticsearch on host to %s and port to %s", elasticsearchProtocol, elasticsearchHost, elasticsearchPort));
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(elasticsearchHost, elasticsearchPort, elasticsearchProtocol)
                )
        );
    }
}

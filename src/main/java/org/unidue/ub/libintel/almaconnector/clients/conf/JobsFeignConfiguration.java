package org.unidue.ub.libintel.almaconnector.clients.conf;

import feign.RequestInterceptor;
import feign.Retryer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.unidue.ub.libintel.almaconnector.clients.AlmaRetryer;
import org.unidue.ub.libintel.almaconnector.clients.ApiKeyAuth;
import org.unidue.ub.libintel.almaconnector.clients.ContentTypeInterceptor;

/**
 * configuration for the Feign clients to allow for basic authentication upon the requests to the settings backend
 * within the libintel architecture.
 */
public class JobsFeignConfiguration {

    @Value("${alma.api.key}")
    private String almaAcqApiKey;

    private static final Logger log = LoggerFactory.getLogger(JobsFeignConfiguration.class);

    /**
     * appropriate request interceptor to add the authentication information.
     * @return the request interceptor
     */
    @Bean
    public RequestInterceptor apiKeyInterceptor() {
        // add authentication information as query parameter 'apikey'
        ApiKeyAuth apiKeyAuth = new ApiKeyAuth("query", "apikey");

        //set the api key as defied in the properties
        apiKeyAuth.setApiKey(almaAcqApiKey);
        return apiKeyAuth;
    }

    /**
     * appropriate request interceptor to add the content type information.
     * @return the request interceptor
     */
    @Bean
    public RequestInterceptor contentTypeInterceptor() {
        return new ContentTypeInterceptor(MediaType.APPLICATION_JSON);
    }

    /**
     * activates the full logging of the Feign-client
     * @return a logger level
     */
    @Bean
    feign.Logger.Level feignLoggerLevel() {
        return feign.Logger.Level.FULL;
    }

    /**
     * add the retryer bean to allow for retries upon errors connecting to alma API
     * @return the AlmaRetryer
     */
    @Bean
    public Retryer retryer() {
        return new AlmaRetryer();
    }
}

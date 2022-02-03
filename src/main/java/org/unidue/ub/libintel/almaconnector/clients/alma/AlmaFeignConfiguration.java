package org.unidue.ub.libintel.almaconnector.clients.alma;

import feign.RequestInterceptor;
import feign.Retryer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.unidue.ub.libintel.almaconnector.clients.ApiKeyAuth;

/**
 * configuration for the Feign clients used to query the Alma APIs. Sets the API key authentication, the Logging level and the retryer
 */
public class AlmaFeignConfiguration {

    @Value("${libintel.alma.api.key.general}")
    private String almaApiKey;

    /**
     * appropriate request interceptor to add the authentication information.
     * @return the request interceptor
     */
    @Bean
    public RequestInterceptor apiKeyInterceptor() {
        // add authentication information as query parameter 'apikey'
        ApiKeyAuth apiKeyAuth = new ApiKeyAuth("query", "apikey");

        //set the api key as defied in the properties
        apiKeyAuth.setApiKey(almaApiKey);
        return apiKeyAuth;
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

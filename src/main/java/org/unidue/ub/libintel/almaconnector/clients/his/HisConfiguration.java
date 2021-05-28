package org.unidue.ub.libintel.almaconnector.clients.his;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.unidue.ub.libintel.almaconnector.clients.ApiKeyAuth;

@Configuration
public class HisConfiguration {

    @Value("${alma.register.api.key}")
    private String almaRegisterApiKey;

    /**
     * appropriate request interceptor to add the authentication information.
     * @return the request interceptor
     */
    @Bean
    public RequestInterceptor apiKeyInterceptor() {
        // add authentication information as query parameter 'apikey'
        ApiKeyAuth apiKeyAuth = new ApiKeyAuth("query", "apikey");

        //set the api key as defied in the properties
        apiKeyAuth.setApiKey(almaRegisterApiKey);
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
}

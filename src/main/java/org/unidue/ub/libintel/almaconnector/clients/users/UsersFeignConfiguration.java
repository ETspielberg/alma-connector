package org.unidue.ub.libintel.almaconnector.clients.users;

import feign.RequestInterceptor;
import feign.Retryer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.unidue.ub.libintel.almaconnector.clients.AlmaRetryer;
import org.unidue.ub.libintel.almaconnector.clients.ApiKeyAuth;

/**
 * configuration for the Feign clients to allow for basic authentication upon the requests to the settings backend
 * within the libintel architecture.
 */
public class UsersFeignConfiguration {

    @Value("${alma.api.users.key}")
    private String almaUsersApiKey;

    private static final Logger log = LoggerFactory.getLogger(UsersFeignConfiguration.class);

    /**
     * appropriate request interceptor to add the authentication information.
     * @return the request interceptor
     */
    @Bean
    public RequestInterceptor apiKeyInterceptor() {
        // add authentication information as query parameter 'apikey'
        ApiKeyAuth apiKeyAuth = new ApiKeyAuth("query", "apikey");

        //set the api key as defied in the properties
        apiKeyAuth.setApiKey(almaUsersApiKey);
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

    @Bean
    public Retryer retryer() {
        return new AlmaRetryer();
    }
}

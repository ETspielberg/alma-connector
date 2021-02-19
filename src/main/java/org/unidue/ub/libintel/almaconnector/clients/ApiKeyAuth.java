package org.unidue.ub.libintel.almaconnector.clients;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * intercepts each sent request and adds the authentication via api key
 */
public class ApiKeyAuth implements RequestInterceptor {
    private final String location;
    private final String paramName;

    private String apiKey;

    /**
     * adds the necessary authentication parameter to the API call
     * @param location the place where to put the authentication data. possible values are 'query', 'header' or 'cookie'
     * @param paramName the pparametername for the authentication data, i.e. the query parameter, header or cookie name.
     */
    public ApiKeyAuth(String location, String paramName) {
        this.location = location;
        this.paramName = paramName;
    }

    /**
     * sets the api key for the API used
     * @param apiKey the api key
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * applies the API key authentication to each request. Depending on the location parameter the API key is added
     * to the query, the header or the cookie with the name defined by the parameter paramName.
     * @param template the request template the authentication data are added to.
     */
    @Override
    public void apply(RequestTemplate template) {
        if ("query".equals(location)) {
            template.query(paramName, apiKey);
        } else if ("header".equals(location)) {
            template.header(paramName, apiKey);
        } else if ("cookie".equals(location)) {
            template.header("Cookie", String.format("%s=%s", paramName, apiKey));
        }
    }
}

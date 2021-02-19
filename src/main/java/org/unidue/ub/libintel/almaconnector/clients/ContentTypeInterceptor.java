package org.unidue.ub.libintel.almaconnector.clients;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.http.MediaType;

/**
 * intercepts each sent request and adds the authentication via api key
 */
public class ContentTypeInterceptor implements RequestInterceptor {

    private MediaType mediaType;

    public ContentTypeInterceptor(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    /**
     * applies the API key authentication to each request. Depending on the location parameter the API key is added
     * to the query, the header or the cookie with the name defined by the parameter paramName.
     * @param template the request template the authentication data are added to.
     */
    @Override
    public void apply(RequestTemplate template) {
        template.header("Content-Type", mediaType.getType());
        template.header("Accept", mediaType.getType());
    }
}

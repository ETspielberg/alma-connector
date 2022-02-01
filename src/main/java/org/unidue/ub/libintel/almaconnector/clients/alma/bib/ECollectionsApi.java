package org.unidue.ub.libintel.almaconnector.clients.alma.bib;

import feign.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.unidue.ub.libintel.almaconnector.clients.alma.AlmaFeignConfiguration;

@FeignClient(name = "eCollections", url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1/bibs", configuration = AlmaFeignConfiguration.class)
@Service
public interface ECollectionsApi {


    /**
     * Retrieve Electronic Collections for Bib record
     * This web service returns Electronic Collections for a Bib.
     *
     * @param mmsId  The Bib Record ID. (required)
     * @param limit  Limits the number of results. Optional. Valid values are 0-100. Default value: 10. (optional)
     * @param offset Offset of the results returned. Optional. Default value: 0, which means that the first results will be returned. (optional)
     * @return Object
     */
    @RequestMapping(method = RequestMethod.GET,
            value = "/{mmsId}/e-collections",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    Object getBibsMmsIdECollections(@PathVariable("mmsId") String mmsId,
                                    @RequestParam("limit") Integer limit,
                                    @RequestParam("offset") Integer offset);


    /**
     * Retrieve Electronic Collection
     * This web service returns an Electronic Collection for a Bib ID and an Electronic Collection ID.
     *
     * @param mmsId        The Bib Record ID. (required)
     * @param collectionId The Electronic Collection ID. (required)
     * @return Object
     */
    @RequestLine("GET /almaws/v1/bibs/{mmsId}/e-collections/{collectionId}")
    @Headers({
            "Accept: application/json",
    })
    @RequestMapping(method = RequestMethod.GET,
            value = "/{mmsId}/e-collections/{collectionId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    Object getBibsMmsIdECollectionsCollectionId(@PathVariable("mmsId") String mmsId,
                                                @PathVariable("collectionId") String collectionId);
}

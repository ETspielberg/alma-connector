package org.unidue.ub.libintel.almaconnector.clients.alma.conf;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.unidue.ub.libintel.almaconnector.clients.alma.AlmaFeignConfiguration;

@FeignClient(name = "physicalLocation", url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1/conf/libraries", configuration = AlmaFeignConfiguration.class)
@Service
public interface PhysicalLocationApi {


    /**
     * Update a Location
     * This Web service updates a Physical Location.
     *
     * @param libraryCode  The code of the library for which the location belongs to. (required)
     * @param locationCode The code of the location to be retrieved. (required)
     * @param body         This method takes a location object. See [here](/alma/apis/docs/xsd/rest_location.xsd?tags&#x3D;PUT) (required)
     * @return Object
     */
    @RequestMapping(method = RequestMethod.PUT,
            value = "/{libraryCode}/locations/{locationCode}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    Object putConfLibrariesLibraryCodeLocationsLocationCode(@PathVariable("libraryCode") String libraryCode,
                                                            @PathVariable("locationCode") String locationCode,
                                                            @RequestBody Object body);
}

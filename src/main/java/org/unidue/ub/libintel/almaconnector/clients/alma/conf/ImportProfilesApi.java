package org.unidue.ub.libintel.almaconnector.clients.alma.conf;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.unidue.ub.libintel.almaconnector.clients.alma.AlmaFeignConfiguration;

@FeignClient(name = "importProfiles", url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1/conf/md-import-profiles", configuration = AlmaFeignConfiguration.class)
@Service
public interface ImportProfilesApi {


    /**
     * Retrieve Import Profiles
     * This Web service returns a list of Import Profiles. In default mode, with no query parameters, all import profiles are returned.
     *
     * @param type   The Import Profile Type. Optional. Default is no Import Type. (optional, default to &quot;&quot;)
     * @param ieType The type of IE Entity created by this import. Optional. Default is to include all entities. (optional, default to &quot;ALL&quot;)
     * @return Object
     */
    @RequestMapping(method = RequestMethod.GET,
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    Object getConfMdImportProfiles(@RequestParam("type") String type,
                                   @RequestParam("ie_type") String ieType);

    /**
     * Retrieve Import Profile
     * This web service returns an Import profile given a Profile ID.
     *
     * @param profileId The profile identifier. (required)
     * @return Object
     */
    @RequestMapping(method = RequestMethod.GET,
            value = "/{profileId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    Object getConfMdImportProfilesProfileId(@PathVariable("profileId") String profileId);

    /**
     * MD Import op - Deprecated
     * DEPRECATED - use the &#39;Submit Job&#39; API instead.   This web service runs an MD import according to a defined Import Profile. Supported for FTP, OAI and Digital.
     *
     * @param profileId The profile identifier. (required)
     * @param op        The operation to perform on the import profile. The operation op&#x3D;run is supported. (required)
     * @return Object
     */
    @RequestMapping(method = RequestMethod.POST,
            value = "/{profileId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    Object postConfMdImportProfilesProfileId(@PathVariable("profileId") String profileId,
                                             @RequestParam("op") String op);
}

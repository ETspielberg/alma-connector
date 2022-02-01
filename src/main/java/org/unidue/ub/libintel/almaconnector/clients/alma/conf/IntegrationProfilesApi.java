package org.unidue.ub.libintel.almaconnector.clients.alma.conf;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.unidue.ub.libintel.almaconnector.clients.alma.AlmaFeignConfiguration;

@FeignClient(name = "integrationProfiles", url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1/conf/integration-profiles", configuration = AlmaFeignConfiguration.class)
@Service
public interface IntegrationProfilesApi {


  /**
   * Retrieve a list of Integration Profiles
   * This Web service returns a list of Integration Profiles.  
   * @param type Type for filtering. Optional. Valid values are from the IntegrationTypes code table (optional, default to &quot;&quot;)
   * @param q Search query. Optional. Searching for words in created_by or name (see [Brief Search](https://developers.exlibrisgroup.com/blog/How-we-re-building-APIs-at-Ex-Libris#BriefSearch)) (optional, default to &quot;&quot;)
   * @param limit Limits the number of results. Optional. Valid values are 0-100. Default value: 10. (optional)
   * @param offset Offset of the results returned. Optional. Default value: 0, which means that the first results will be returned. (optional)
   * @return Object
   */
  @RequestMapping(method= RequestMethod.GET,
          value="",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getonfIntegrationProfiles(@RequestParam("type") String type,
                                   @RequestParam("q") String q,
                                   @RequestParam("limit") Integer limit,
                                   @RequestParam("offset") Integer offset);


  /**
   * Retrieve an Integration Profile
   * This Web service returns an Integration Profile given an Integration Profile ID.  
   * @param id The Integration Profile ID (required)
   * @return Object
   */
  @RequestMapping(method=RequestMethod.GET,
          value="/{id}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getConfIntegrationProfilesId(@PathVariable("id") String id);
}

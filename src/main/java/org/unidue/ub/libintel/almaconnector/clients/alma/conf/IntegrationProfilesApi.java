package org.unidue.ub.libintel.almaconnector.clients.alma.conf;

import feign.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.almaconnector.clients.alma.AlmaFeignConfiguration;

@FeignClient(name = "integrationProfiles", url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1/integration-profiles", configuration = AlmaFeignConfiguration.class)
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
  @RequestLine("GET /almaws/v1/conf/integration-profiles?type={type}&q={q}&limit={limit}&offset={offset}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1ConfIntegrationProfiles(@Param("type") String type, @Param("q") String q, @Param("limit") Integer limit, @Param("offset") Integer offset);


  /**
   * Retrieve an Integration Profile
   * This Web service returns an Integration Profile given an Integration Profile ID.  
   * @param id The Integration Profile ID (required)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/conf/integration-profiles/{id}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1ConfIntegrationProfilesId(@Param("id") String id);
}

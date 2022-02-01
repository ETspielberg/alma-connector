package org.unidue.ub.libintel.almaconnector.clients.alma.bib;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.unidue.ub.libintel.almaconnector.clients.alma.AlmaFeignConfiguration;

@FeignClient(name = "authorities", url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1/authorities", configuration = AlmaFeignConfiguration.class)
@Service
public interface AuthoritiesApi {


  /**
   * Delete Authority Record
   * This web service deletes an Authority Record.  For more information regarding the various options supported for this API see [here](https://developers.exlibrisgroup.com/blog/cataloging-APIs-enhancements).
   * @param authorityRecordId The Authority Record ID. (required)
   * @param override Override the warnings and delete the Authority Record. Optional.  The default is to not override (false). (optional, default to &quot;false&quot;)
   * @param catalogerLevel Cataloger level of the user deleting the record. (optional, default to &quot;&quot;)
   */
  @RequestMapping(method = RequestMethod.DELETE,
          value = "/authorities/{authorityRecordId}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  void deleteBibsAuthoritiesAuthorityRecordId(@PathVariable("authorityRecordId") String authorityRecordId,
                                              @RequestParam("override") String override,
                                              @RequestParam("cataloger_level") String catalogerLevel);


  /**
   * Retrieve Authorities
   * This web service returns a list of Authority records.
   * @param originatingSystem The originating system. System in which the record was initially generated. Optional. But one of the three parameters is required. (optional, default to &quot;&quot;)
   * @param originatingSystemId The originating system ID. The ID of the record in the original system. Optional. But originating system is required if this is present. (optional, default to &quot;&quot;)
   * @param otherSystemId The other system ID. An additional ID stored as part of the record&#39;s network numbers. Optional. (optional, default to &quot;&quot;)
   * @param limit Limits the number of results. Optional. Valid values are 0-100. Default value: 10. (optional)
   * @param offset Offset of the results returned. Optional. Default value: 0, which means that the first results will be returned. (optional)
   * @param view Use view&#x3D;brief to retrieve without the MARCXML. (optional, default to &quot;full&quot;)
   * @return Object
   */
  @RequestMapping(method = RequestMethod.GET,
          value = "/authorities",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getBibsAuthorities(@RequestParam("originating_system") String originatingSystem,
                            @RequestParam("originating_system_id") String originatingSystemId,
                            @RequestParam("other_system_id") String otherSystemId,
                            @RequestParam("limit") Integer limit,
                            @RequestParam("offset") Integer offset,
                            @RequestParam("view") String view);


  /**
   * Retrieve Authority Record
   * This web service returns an Authority record.
   * @param authorityRecordId The Authority Record ID. (required)
   * @param view Use view&#x3D;brief to retrieve without the MARCXML. (optional, default to &quot;full&quot;)
   * @return Object
   */
  @RequestMapping(method = RequestMethod.GET,
          value = "/authorities/{authorityRecordId}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getBibsAuthoritiesAuthorityRecordId(@PathVariable("authorityRecordId") String authorityRecordId,
                                             @RequestParam("view") String view);


  /**
   * Create Authority Record
   * This web service creates a new Authority record.   Note: JSON is not supported for this API.  For more information regarding the various options supported for this API see [here](https://developers.exlibrisgroup.com/blog/cataloging-APIs-enhancements).
   * @param body This method takes a Authority object. See [here](/alma/apis/docs/xsd/rest_authority.xsd?tags&#x3D;POST) (required)
   * @param normalization The id of the normalization profile to run. (optional, default to &quot;&quot;)
   * @param validate Boolean flag for indicating whether to validate the record. Default is: false. (optional, default to &quot;false&quot;)
   * @param overrideWarning Override the warning(s). Ignored if validate&#x3D;false. Default is: true (optional, default to &quot;true&quot;)
   * @param checkMatch Indicating whether to check for a match. Default: false (record will be saved despite possible match). (optional, default to &quot;false&quot;)
   * @return Object
   */
  @RequestMapping(method = RequestMethod.POST,
          value = "/authorities",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object postBibsAuthorities(@RequestBody Object body,
                             @RequestParam("normalization") String normalization,
                             @RequestParam("validate") String validate,
                             @RequestParam("override_warning") String overrideWarning,
                             @RequestParam("check_match") String checkMatch);


  /**
   * Update Authority Record
   * This web service updates an Authority Record.   Note: JSON is not supported.  For more information regarding the various options supported for this API see [here](https://developers.exlibrisgroup.com/blog/cataloging-APIs-enhancements).  See [Working with APIs in a Network Topology](https://developers.exlibrisgroup.com/blog/Working-with-APIs-in-a-Network-Topology) for more details.
   * @param authorityRecordId The Authority Record ID. (required)
   * @param body This method takes an Authority object. See [here](/alma/apis/docs/xsd/rest_authority.xsd?tags&#x3D;PUT) (required)
   * @param normalization The id of the normalization profile to run. (optional, default to &quot;&quot;)
   * @param validate Boolean flag for indicating whether to validate the record. Default is: false. (optional, default to &quot;false&quot;)
   * @param overrideWarning Override the warning(s). Ignored if validate&#x3D;false. Default is: true (optional, default to &quot;true&quot;)
   * @param overrideLock Override the record lock. Default is: true (record will be saved regardless if is currently being edited by another user). (optional, default to &quot;true&quot;)
   * @param staleVersionCheck Check for record identical to one in the database. Default is: false (When true, the 005 field of the MARC record must be identical to that of the record in the database.)  (optional, default to &quot;false&quot;)
   * @param catalogerLevel Catalog level of the user updating the record - used for validating that the level is sufficient. To change the record&#39;s level, use the payload&#39;s cataloging_level field.  (optional, default to &quot;&quot;)
   * @param checkMatch Indicating whether to check for a match. Default: false (record will be saved despite possible match). (optional, default to &quot;false&quot;)
   * @return Object
   */
  @RequestMapping(method = RequestMethod.PUT,
          value = "/authorities/{authorityRecordId}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object putBibsAuthoritiesAuthorityRecordId(@PathVariable("authorityRecordId") String authorityRecordId,
                                             @RequestBody Object body,
                                             @RequestParam("normalization") String normalization,
                                             @RequestParam("validate") String validate,
                                             @RequestParam("override_warning") String overrideWarning,
                                             @RequestParam("override_lock") String overrideLock,
                                             @RequestParam("stale_version_check") String staleVersionCheck,
                                             @RequestParam("cataloger_level") String catalogerLevel,
                                             @RequestParam("check_match") String checkMatch);
}

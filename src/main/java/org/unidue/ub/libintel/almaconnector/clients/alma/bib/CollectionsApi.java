package org.unidue.ub.libintel.almaconnector.clients.alma.bib;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.unidue.ub.libintel.almaconnector.clients.alma.AlmaFeignConfiguration;

@FeignClient(name = "collections", url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1/bibs/collections", configuration = AlmaFeignConfiguration.class)
@Service
public interface CollectionsApi {


  /**
   * Remove a collection with no Bibs
   * This Web service removes a collection that has no Sub Collections and no Bibliographic titles attached.
   * @param pid The collection ID. (required)
   */
  @RequestMapping(method = RequestMethod.DELETE,
          value = "/collections/{pid}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  void deleteBibsCollectionsPid(@PathVariable("pid") String pid);

  /**
   * Remove Bib from a collection
   * This Web service removes a bibliographic title from a collection.
   * @param pid The collection ID. (required)
   * @param mmsId The Bib Record ID (for example, 99939650000541). (required)
   */
  @RequestMapping(method = RequestMethod.DELETE,
          value = "/collections/{pid}/bibs/{mmsId}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  void deleteBibsCollectionsPidBibsMmsId(@PathVariable("pid") String pid,
                                         @PathVariable("mmsId") String mmsId);

  /**
   * Retrieve Collections
   * This Web service returns a list of collections.
   * @param level The number of levels of sub-collections to retrieve. Optional. For example, 1 &#x3D; only current; 2 &#x3D; immediate decendants. Does not work with query. Default is 1. (required)
   * @param q Search query. Optional.  Does not work with levels parameter. Searching for text in library, collection name, external system or external ID. Multiple search terms may be combined with AND only.  For example q&#x3D;external_system~x%20AND%20external_id~y. (optional, default to &quot;&quot;)
   * @return Object
   */
  @RequestMapping(method = RequestMethod.GET,
          value = "/collections",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getBibsCollections(@RequestParam("level") String level,
                            @RequestParam("q") String q);


  /**
   * Retrieve Collection
   * This Web service returns a collection for a given pid.
   * @param pid The collection ID. (required)
   * @param level This parameter determines the number of levels of sub-collections should be retrieved. For example, 1 &#x3D; only current; 2 &#x3D; immediate decendants. Default is 1. (required)
   * @return Object
   */
  @RequestMapping(method = RequestMethod.GET,
          value = "/collections/{pid}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getBibsCollectionsPid(@PathVariable("pid") String pid,
                               @RequestParam("level") String level);


  /**
   * Retrieve Bibs in a collection
   * This Web service returns a list of bibliographic titles in a given collection.
   * @param pid The collection ID. (required)
   * @param offset Offset of the results returned. Optional.Default value: 0, which means that the first results will be returned.  (optional, default to &quot;0&quot;)
   * @param limit Limits the number of results. Optional. Valid values are 0-100. Default value: 10. (optional)
   * @return Object
   */
  @RequestMapping(method = RequestMethod.GET,
          value = "/collections/{pid}/bibs",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getBibsCollectionsPidBibs(@PathVariable("pid") String pid,
                                   @RequestParam("offset") String offset,
                                   @RequestParam("limit") Integer limit);


  /**
   * Create Collection
   * This Web service creates a collection.
   * @param body This method takes a Collection object. See [here](/alma/apis/docs/xsd/rest_collection.xsd?tags&#x3D;POST) (required)
   * @param recordFormat The record format which may be marc21, unimarc, kormarc, cnmarc, dc, dcap01, dcap02, or etd. (optional, default to &quot;marc21&quot;)
   * @return Object
   */
  @RequestMapping(method = RequestMethod.POST,
          value = "/collections",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object postBibsCollections(@RequestBody Object body,
                             @RequestParam("record_format") String recordFormat);


  /**
   * Add Bib to a collection
   * This Web service adds a bibliographic title into a given collection.
   * @param pid The collection ID. (required)
   * @param body This method takes an Bib object with only mms_id. See [here](/alma/apis/docs/xsd/rest_bib.xsd?tags&#x3D;POST) (required)
   * @return Object
   */
  @RequestMapping(method = RequestMethod.POST,
          value = "/collections/{pid}/bibs",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object postBibsCollectionsPidBibs(@PathVariable("pid") String pid,
                                    @RequestBody Object body);

  /**
   * Update Collection
   * This Web service updates a collection.
   * @param pid The collection ID. (required)
   * @param body This method takes a Collection object. See [here](/alma/apis/docs/xsd/rest_collection.xsd?tags&#x3D;PUT) (required)
   * @return Object
   */
  @RequestMapping(method = RequestMethod.PUT,
          value = "/collections/{pid}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object putBibsCollectionsPid(@PathVariable("pid") String pid,
                               @RequestBody Object body);
}

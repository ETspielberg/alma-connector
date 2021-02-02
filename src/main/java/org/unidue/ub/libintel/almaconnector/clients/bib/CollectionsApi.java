package org.unidue.ub.libintel.almaconnector.clients.bib;

import feign.*;


public interface CollectionsApi {


  /**
   * Remove a collection with no Bibs
   * This Web service removes a collection that has no Sub Collections and no Bibliographic titles attached.
   * @param pid The collection ID. (required)
   */
  @RequestLine("DELETE /almaws/v1/bibs/collections/{pid}")
  @Headers({
    "Accept: application/json",
  })
  void deleteAlmawsV1BibsCollectionsPid(@Param("pid") String pid);

  /**
   * Remove Bib from a collection
   * This Web service removes a bibliographic title from a collection.
   * @param pid The collection ID. (required)
   * @param mmsId The Bib Record ID (for example, 99939650000541). (required)
   */
  @RequestLine("DELETE /almaws/v1/bibs/collections/{pid}/bibs/{mmsId}")
  @Headers({
    "Accept: application/json",
  })
  void deleteAlmawsV1BibsCollectionsPidBibsMmsId(@Param("pid") String pid, @Param("mms_id") String mmsId);

  /**
   * Retrieve Collections
   * This Web service returns a list of collections.
   * @param level The number of levels of sub-collections to retrieve. Optional. For example, 1 &#x3D; only current; 2 &#x3D; immediate decendants. Does not work with query. Default is 1. (required)
   * @param q Search query. Optional.  Does not work with levels parameter. Searching for text in library, collection name, external system or external ID. Multiple search terms may be combined with AND only.  For example q&#x3D;external_system~x%20AND%20external_id~y. (optional, default to &quot;&quot;)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/bibs/collections?level={level}&q={q}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1BibsCollections(@Param("level") String level, @Param("q") String q);


  /**
   * Retrieve Collection
   * This Web service returns a collection for a given pid.
   * @param pid The collection ID. (required)
   * @param level This parameter determines the number of levels of sub-collections should be retrieved. For example, 1 &#x3D; only current; 2 &#x3D; immediate decendants. Default is 1. (required)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/bibs/collections/{pid}?level={level}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1BibsCollectionsPid(@Param("pid") String pid, @Param("level") String level);


  /**
   * Retrieve Bibs in a collection
   * This Web service returns a list of bibliographic titles in a given collection.
   * @param pid The collection ID. (required)
   * @param offset Offset of the results returned. Optional.Default value: 0, which means that the first results will be returned.  (optional, default to &quot;0&quot;)
   * @param limit Limits the number of results. Optional. Valid values are 0-100. Default value: 10. (optional)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/bibs/collections/{pid}/bibs?offset={offset}&limit={limit}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1BibsCollectionsPidBibs(@Param("pid") String pid, @Param("offset") String offset, @Param("limit") Integer limit);


  /**
   * Create Collection
   * This Web service creates a collection.
   * @param body This method takes a Collection object. See [here](/alma/apis/docs/xsd/rest_collection.xsd?tags&#x3D;POST) (required)
   * @param recordFormat The record format which may be marc21, unimarc, kormarc, cnmarc, dc, dcap01, dcap02, or etd. (optional, default to &quot;marc21&quot;)
   * @return Object
   */
  @RequestLine("POST /almaws/v1/bibs/collections?record_format={recordFormat}")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  Object postAlmawsV1BibsCollections(Object body, @Param("record_format") String recordFormat);


  /**
   * Add Bib to a collection
   * This Web service adds a bibliographic title into a given collection.
   * @param pid The collection ID. (required)
   * @param body This method takes an Bib object with only mms_id. See [here](/alma/apis/docs/xsd/rest_bib.xsd?tags&#x3D;POST) (required)
   * @return Object
   */
  @RequestLine("POST /almaws/v1/bibs/collections/{pid}/bibs")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  Object postAlmawsV1BibsCollectionsPidBibs(@Param("pid") String pid, Object body);

  /**
   * Update Collection
   * This Web service updates a collection.
   * @param pid The collection ID. (required)
   * @param body This method takes a Collection object. See [here](/alma/apis/docs/xsd/rest_collection.xsd?tags&#x3D;PUT) (required)
   * @return Object
   */
  @RequestLine("PUT /almaws/v1/bibs/collections/{pid}")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  Object putAlmawsV1BibsCollectionsPid(@Param("pid") String pid, Object body);
}

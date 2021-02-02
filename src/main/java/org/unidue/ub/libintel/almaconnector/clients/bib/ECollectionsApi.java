package org.unidue.ub.libintel.almaconnector.clients.bib;

import feign.*;


public interface ECollectionsApi {


  /**
   * Retrieve Electronic Collections for Bib record
   * This web service returns Electronic Collections for a Bib.
   * @param mmsId The Bib Record ID. (required)
   * @param limit Limits the number of results. Optional. Valid values are 0-100. Default value: 10. (optional)
   * @param offset Offset of the results returned. Optional. Default value: 0, which means that the first results will be returned. (optional)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/bibs/{mmsId}/e-collections?limit={limit}&offset={offset}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1BibsMmsIdECollections(@Param("mms_id") String mmsId, @Param("limit") Integer limit, @Param("offset") Integer offset);


  /**
   * Retrieve Electronic Collection
   * This web service returns an Electronic Collection for a Bib ID and an Electronic Collection ID.
   * @param mmsId The Bib Record ID. (required)
   * @param collectionId The Electronic Collection ID. (required)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/bibs/{mmsId}/e-collections/{collectionId}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1BibsMmsIdECollectionsCollectionId(@Param("mms_id") String mmsId, @Param("collection_id") String collectionId);
}

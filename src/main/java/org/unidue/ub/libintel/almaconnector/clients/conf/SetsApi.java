package org.unidue.ub.libintel.almaconnector.clients.conf;

import feign.*;


public interface SetsApi {


  /**
   * Delete a Set
   * Web service for deleting a set.
   * @param setId Unique id of the set. Mandatory. (required)
   */
  @RequestLine("DELETE /almaws/v1/conf/sets/{setId}")
  @Headers({
    "Accept: application/json",
  })
  void deleteAlmawsV1ConfSetsSetId(@Param("set_id") String setId);

  /**
   * Retrieve a list of Sets
   * This Web service returns a list of Sets.  
   * @param contentType Content type for filtering. Optional. Valid values are from the SetContentType code table (optional, default to &quot;&quot;)
   * @param setType Set type for filtering. Optional. Valid values are &#39;ITEMIZED&#39; or &#39;LOGICAL&#39;. (optional, default to &quot;&quot;)
   * @param q Search query. Optional. Searching for words in created_by or name (see [Brief Search](https://developers.exlibrisgroup.com/blog/How-we-re-building-APIs-at-Ex-Libris#BriefSearch)) (optional, default to &quot;&quot;)
   * @param limit Limits the number of results. Optional. Valid values are 0-100. Default value: 10. (optional)
   * @param offset Offset of the results returned. Optional. Default value: 0, which means that the first results will be returned. (optional)
   * @param setOrigin Set origin for filtering. Optional. Valid values are &#39;UI&#39; or &#39;UI_CZ&#39;. (optional, default to &quot;UI&quot;)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/conf/sets?content_type={contentType}&set_type={setType}&q={q}&limit={limit}&offset={offset}&set_origin={setOrigin}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1ConfSets(@Param("content_type") String contentType, @Param("set_type") String setType, @Param("q") String q, @Param("limit") Integer limit, @Param("offset") Integer offset, @Param("set_origin") String setOrigin);

  /**
   * Retrieve a Set
   * This Web service returns a Set given a Set ID.  
   * @param setId Unique id of the set. Mandatory. (required)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/conf/sets/{setId}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1ConfSetsSetId(@Param("set_id") String setId);

  /**
   * Retrieve Set Members
   * This Web service returns the members of a Set given a Set ID.  
   * @param setId The set ID (required)
   * @param limit Limits the number of results. Optional. Valid values are 0-100. Default value: 10. (optional)
   * @param offset Offset of the results returned. Optional. Default value: 0, which means that the first results will be returned. (optional)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/conf/sets/{setId}/members?limit={limit}&offset={offset}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1ConfSetsSetIdMembers(@Param("set_id") String setId, @Param("limit") Integer limit, @Param("offset") Integer offset);


  /**
   * Create a Set
   * Web service for creating or combining a set.    If you are creating a new set, you can use this API to create 2 types of sets: Itemized set, Logical set  In order to create an itemized set, first create an empty set using this API, and then use the [Manage Members](https://developers.exlibrisgroup.com/alma/apis/docs/conf/UE9TVCAvYWxtYXdzL3YxL2NvbmYvc2V0cy97c2V0X2lkfQ&#x3D;&#x3D;/) API to populate it.  Creating logical sets is supported for Inventory related entities (not supported for PO-Lines, Users etc). Details regarding the syntax for creating Logical Sets can be found [here](https://developers.exlibrisgroup.com/alma/integrations/indexdoc-technical).    In addtion, it is possible to create an itemized set and populate it from a logical set by setting the logical set id in the from_logical_set parameter.  It is also possible to create an itemized set which is based on MD import job by providing job instance id and population.  For more details about MD import itemized set [click here](https://developers.exlibrisgroup.com/blog/Creating-sets-from-MD-import-job-results-using-Alma-API)If you are combining sets, then set1 and set2 must be provided. Those two sets will be combined with a new combined set created.    
   * @param body This method takes a Set object. See [here](/alma/apis/docs/xsd/rest_set.xsd?tags&#x3D;POST) (required)
   * @param population The population on which a set should be created. Optional. (optional, default to &quot;&quot;)
   * @param jobInstanceId The id of md import job instance from which a set should be created. Optional. (optional, default to &quot;&quot;)
   * @param fromLogicalSet An id of a logical set to create an Itemized Set based on it. Optional. (optional, default to &quot;&quot;)
   * @param combine The logical operator. Choose between AND, OR, NOT. Default is AND. Optional (optional, default to &quot;None&quot;)
   * @param set1 The primary combining set. Optional. (optional, default to &quot;None&quot;)
   * @param set2 The secondary combining set. Optional. (optional, default to &quot;None&quot;)
   * @return Object
   */
  @RequestLine("POST /almaws/v1/conf/sets?population={population}&job_instance_id={jobInstanceId}&from_logical_set={fromLogicalSet}&combine={combine}&set1={set1}&set2={set2}")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  Object postAlmawsV1ConfSets(Object body, @Param("population") String population, @Param("job_instance_id") String jobInstanceId, @Param("from_logical_set") String fromLogicalSet, @Param("combine") String combine, @Param("set1") String set1, @Param("set2") String set2);

  /**
   * Manage Members
   * This Web service manages the operations of add, delete and replace for members of a Set given a Set ID.  
   * @param setId Unique id of the set. Mandatory. (required)
   * @param op The operation to perform on the set. Mandatory. The supported operations are add_members, delete_members or replace_members. (required)
   * @param body This method takes a Set object including list of members to add/remove. Up to 1000 members can be supplied. See [here](/alma/apis/docs/xsd/rest_set.xsd?tags&#x3D;POST) (required)
   * @param idType The type of the identifier that is used to identify members. Optional.   For physical items: BARCODE.   For Bib records: SYSTEM_NUMBER, OCLC_NUMBER, ISBN, ISSN. For regular MMS-IDs no need to defined this parameter.   For users: any type that is defined in UserIdentifierTypes Code Table (optional, default to &quot;&quot;)
   * @return Object
   */
  @RequestLine("POST /almaws/v1/conf/sets/{setId}?id_type={idType}&op={op}")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  Object postAlmawsV1ConfSetsSetId(@Param("set_id") String setId, @Param("op") String op, Object body, @Param("id_type") String idType);
}

package org.unidue.ub.libintel.almaconnector.clients.bib;

import feign.*;


public interface DigitalRepresentationsApi {


  /**
   * Delete Representation
   * This web service deletes a Digital Representations.
   * @param mmsId The Bib Record ID. (required)
   * @param repId The Representation ID. (required)
   * @param override Indication whether the representation should be deleted even if warnings exist. Optional. By default: false. (optional, default to &quot;false&quot;)
   * @param bibs Method for handling a bib left without any representations: retain, suppress or delete. Optional. By default: retain. (optional, default to &quot;retain&quot;)
   */
  @RequestLine("DELETE /almaws/v1/bibs/{mmsId}/representations/{repId}?override={override}&bibs={bibs}")
  @Headers({
    "Accept: application/json",
  })
  void deleteAlmawsV1BibsMmsIdRepresentationsRepId(@Param("mms_id") String mmsId, @Param("rep_id") String repId, @Param("override") String override, @Param("bibs") String bibs);

  /**
   * Delete Representation File
   * This web service deletes a Digital Representation File.
   * @param mmsId The Bib Record ID. (required)
   * @param repId The Representation ID. (required)
   * @param fileId The File ID. (required)
   * @param representations Method for handling a representation left without any files: retain or delete. Optional. By default: retain. (optional, default to &quot;retain&quot;)
   * @param bibs Method for handling a bib left without any representations: retain , suppress or delete. Optional. By default: retain. (optional, default to &quot;retain&quot;)
   */
  @RequestLine("DELETE /almaws/v1/bibs/{mmsId}/representations/{repId}/files/{fileId}?representations={representations}&bibs={bibs}")
  @Headers({
    "Accept: application/json",
  })
  void deleteAlmawsV1BibsMmsIdRepresentationsRepIdFilesFileId(@Param("mms_id") String mmsId, @Param("rep_id") String repId, @Param("file_id") String fileId, @Param("representations") String representations, @Param("bibs") String bibs);


  /**
   * Retrieve Representations
   * This web service returns a list of Digital Representations for a given Bib MMS-ID.
   * @param mmsId The Bib Record ID. (required)
   * @param originatingRecordId Filter by the unique ID of the object in the remote repository. (optional, default to &quot;&quot;)
   * @param limit Limits the number of results. Optional. Valid values are 0-100. Default value: 10. (optional)
   * @param offset Offset of the results returned. Optional. Default value: 0, which means that the first results will be returned. (optional)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/bibs/{mmsId}/representations?originating_record_id={originatingRecordId}&limit={limit}&offset={offset}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1BibsMmsIdRepresentations(@Param("mms_id") String mmsId, @Param("originating_record_id") String originatingRecordId, @Param("limit") Integer limit, @Param("offset") Integer offset);


  /**
   * Retrieve Representation Details
   * This web service returns a specific Digital Representation&#39;s details. Supported for Remote and Non-Remote Representations.
   * @param mmsId The Bib Record ID. (required)
   * @param repId The Representation ID. (required)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/bibs/{mmsId}/representations/{repId}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1BibsMmsIdRepresentationsRepId(@Param("mms_id") String mmsId, @Param("rep_id") String repId);

  /**
   * Retrieve Representation Files&#39; Details
   * This web service returns a specific Representation Files&#39; details. Supported for Non-Remote Representations.
   * @param mmsId The Bib Record ID. (required)
   * @param repId The Representation ID. (required)
   * @param expand If expand&#x3D;url the &lt;url&gt; field will hold the signed URL for downloading. (optional, default to &quot;&quot;)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/bibs/{mmsId}/representations/{repId}/files?expand={expand}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1BibsMmsIdRepresentationsRepIdFiles(@Param("mms_id") String mmsId, @Param("rep_id") String repId, @Param("expand") String expand);


  /**
   * Retrieve Representation File Details
   * This web service returns a specific Representation File details. Supported for Non-Remote Representations.
   * @param mmsId The Bib Record ID. (required)
   * @param repId The Representation ID. (required)
   * @param fileId The File ID. (required)
   * @param expand If expand&#x3D;url the &lt;url&gt; field will hold the signed URL for downloading. (optional, default to &quot;&quot;)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/bibs/{mmsId}/representations/{repId}/files/{fileId}?expand={expand}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1BibsMmsIdRepresentationsRepIdFilesFileId(@Param("mms_id") String mmsId, @Param("rep_id") String repId, @Param("file_id") String fileId, @Param("expand") String expand);


  /**
   * Create Representation
   * This web service creates a Digital Representation.
   * @param mmsId The Bib Record ID. (required)
   * @param body This method takes a Representation object. See [here](/alma/apis/docs/xsd/rest_representation.xsd?tags&#x3D;POST) (required)
   * @param generateLabel Auto-generate label: true/false (optional, default to &quot;false&quot;)
   * @return Object
   */
  @RequestLine("POST /almaws/v1/bibs/{mmsId}/representations?generate_label={generateLabel}")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  Object postAlmawsV1BibsMmsIdRepresentations(@Param("mms_id") String mmsId, Object body, @Param("generate_label") String generateLabel);


  /**
   * Create Representation File
   * This web service creates a Digital Representation File.
   * @param mmsId The Bib Record ID. (required)
   * @param repId The Representation ID. (required)
   * @param body This method takes a Representation File object. See [here](/alma/apis/docs/xsd/rest_representation_file.xsd?tags&#x3D;POST) (required)
   * @return Object
   */
  @RequestLine("POST /almaws/v1/bibs/{mmsId}/representations/{repId}/files")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  Object postAlmawsV1BibsMmsIdRepresentationsRepIdFiles(@Param("mms_id") String mmsId, @Param("rep_id") String repId, Object body);

  /**
   * Update Representation
   * This web service updates a Digital Representations.
   * @param mmsId The Bib Record ID. (required)
   * @param repId The Representation ID. (required)
   * @param body This method takes a Representation object. See [here](/alma/apis/docs/xsd/rest_representation.xsd?tags&#x3D;PUT) (required)
   * @param generateLabel Auto-generate label: true/false (optional, default to &quot;false&quot;)
   * @return Object
   */
  @RequestLine("PUT /almaws/v1/bibs/{mmsId}/representations/{repId}?generate_label={generateLabel}")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  Object putAlmawsV1BibsMmsIdRepresentationsRepId(@Param("mms_id") String mmsId, @Param("rep_id") String repId, Object body, @Param("generate_label") String generateLabel);

  /**
   * Update Representation File
   * This web service updates a Digital Representation File.
   * @param mmsId The Bib Record ID. (required)
   * @param repId The Representation ID. (required)
   * @param fileId The File ID. (required)
   * @param body This method takes a Representation File object. See [here](/alma/apis/docs/xsd/rest_representation_file.xsd?tags&#x3D;PUT) (required)
   * @return Object
   */
  @RequestLine("PUT /almaws/v1/bibs/{mmsId}/representations/{repId}/files/{fileId}")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  Object putAlmawsV1BibsMmsIdRepresentationsRepIdFilesFileId(@Param("mms_id") String mmsId, @Param("rep_id") String repId, @Param("file_id") String fileId, Object body);
}

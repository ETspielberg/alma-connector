package org.unidue.ub.libintel.almaconnector.clients.alma.bib;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.unidue.ub.libintel.almaconnector.clients.alma.AlmaFeignConfiguration;

@FeignClient(name = "digitalRepresentations", url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1/bibs", configuration = AlmaFeignConfiguration.class)
@Service
public interface DigitalRepresentationsApi {


  /**
   * Delete Representation
   * This web service deletes a Digital Representations.
   * @param mmsId The Bib Record ID. (required)
   * @param repId The Representation ID. (required)
   * @param override Indication whether the representation should be deleted even if warnings exist. Optional. By default: false. (optional, default to &quot;false&quot;)
   * @param bibs Method for handling a bib left without any representations: retain, suppress or delete. Optional. By default: retain. (optional, default to &quot;retain&quot;)
   */
  @RequestMapping(method = RequestMethod.DELETE,
          value = "/{mmsId}/representations/{repId}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  void deleteBibsMmsIdRepresentationsRepId(@PathVariable("mmsId") String mmsId,
                                                   @PathVariable("repId") String repId,
                                                   @RequestParam("override") String override,
                                                   @RequestParam("bibs") String bibs);

  /**
   * Delete Representation File
   * This web service deletes a Digital Representation File.
   * @param mmsId The Bib Record ID. (required)
   * @param repId The Representation ID. (required)
   * @param fileId The File ID. (required)
   * @param representations Method for handling a representation left without any files: retain or delete. Optional. By default: retain. (optional, default to &quot;retain&quot;)
   * @param bibs Method for handling a bib left without any representations: retain , suppress or delete. Optional. By default: retain. (optional, default to &quot;retain&quot;)
   */
  @RequestMapping(method = RequestMethod.DELETE,
          value = "/{mmsId}/representations/{repId}/files/{fileId}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  void deleteBibsMmsIdRepresentationsRepIdFilesFileId(@PathVariable("mmsId") String mmsId,
                                                              @PathVariable("repId") String repId, 
                                                              @PathVariable("fileId") String fileId,
                                                              @RequestParam("representations") String representations,
                                                              @RequestParam("bibs") String bibs);


  /**
   * Retrieve Representations
   * This web service returns a list of Digital Representations for a given Bib MMS-ID.
   * @param mmsId The Bib Record ID. (required)
   * @param originatingRecordId Filter by the unique ID of the object in the remote repository. (optional, default to &quot;&quot;)
   * @param limit Limits the number of results. Optional. Valid values are 0-100. Default value: 10. (optional)
   * @param offset Offset of the results returned. Optional. Default value: 0, which means that the first results will be returned. (optional)
   * @return Object
   */
  @RequestMapping(method = RequestMethod.GET,
          value = "/{mmsId}/representations",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getBibsMmsIdRepresentations(@PathVariable("mmsId") String mmsId, 
                                             @RequestParam("originating_record_id") String originatingRecordId, 
                                             @RequestParam("limit") Integer limit, 
                                             @RequestParam("offset") Integer offset);


  /**
   * Retrieve Representation Details
   * This web service returns a specific Digital Representation&#39;s details. Supported for Remote and Non-Remote Representations.
   * @param mmsId The Bib Record ID. (required)
   * @param repId The Representation ID. (required)
   * @return Object
   */
  @RequestMapping(method = RequestMethod.GET,
          value = "/{mmsId}/representations/{repId}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getBibsMmsIdRepresentationsRepId(@PathVariable("mmsId") String mmsId,
                                                  @PathVariable("repId") String repId);

  /**
   * Retrieve Representation Files&#39; Details
   * This web service returns a specific Representation Files&#39; details. Supported for Non-Remote Representations.
   * @param mmsId The Bib Record ID. (required)
   * @param repId The Representation ID. (required)
   * @param expand If expand&#x3D;url the &lt;url&gt; field will hold the signed URL for downloading. (optional, default to &quot;&quot;)
   * @return Object
   */
  @RequestMapping(method = RequestMethod.GET,
          value = "/{mmsId}/representations/{repId}/files",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getBibsMmsIdRepresentationsRepIdFiles(@PathVariable("mmsId") String mmsId, 
                                                       @PathVariable("repId") String repId, 
                                                       @RequestParam("expand") String expand);


  /**
   * Retrieve Representation File Details
   * This web service returns a specific Representation File details. Supported for Non-Remote Representations.
   * @param mmsId The Bib Record ID. (required)
   * @param repId The Representation ID. (required)
   * @param fileId The File ID. (required)
   * @param expand If expand&#x3D;url the &lt;url&gt; field will hold the signed URL for downloading. (optional, default to &quot;&quot;)
   * @return Object
   */
  @RequestMapping(method = RequestMethod.GET,
          value = "/{mmsId}/representations/{repId}/files/{fileId}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getBibsMmsIdRepresentationsRepIdFilesFileId(@PathVariable("mmsId") String mmsId, 
                                                             @PathVariable("repId") String repId, 
                                                             @PathVariable("fileId") String fileId, 
                                                             @RequestParam("expand") String expand);


  /**
   * Create Representation
   * This web service creates a Digital Representation.
   * @param mmsId The Bib Record ID. (required)
   * @param body This method takes a Representation object. See [here](/alma/apis/docs/xsd/rest_representation.xsd?tags&#x3D;POST) (required)
   * @param generateLabel Auto-generate label: true/false (optional, default to &quot;false&quot;)
   * @return Object
   */
  @RequestMapping(method = RequestMethod.POST,
          value = "/{mmsId}/representations",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object postBibsMmsIdRepresentations(@PathVariable("mmsId") String mmsId, Object body, 
                                              @RequestParam("generate_label") String generateLabel);


  /**
   * Create Representation File
   * This web service creates a Digital Representation File.
   * @param mmsId The Bib Record ID. (required)
   * @param repId The Representation ID. (required)
   * @param body This method takes a Representation File object. See [here](/alma/apis/docs/xsd/rest_representation_file.xsd?tags&#x3D;POST) (required)
   * @return Object
   */
  @RequestMapping(method = RequestMethod.POST,
          value = "/{mmsId}/representations/{repId}/files",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object postBibsMmsIdRepresentationsRepIdFiles(@PathVariable("mmsId") String mmsId, 
                                                        @PathVariable("repId") String repId, 
                                                        @RequestBody Object body);

  /**
   * Update Representation
   * This web service updates a Digital Representations.
   * @param mmsId The Bib Record ID. (required)
   * @param repId The Representation ID. (required)
   * @param body This method takes a Representation object. See [here](/alma/apis/docs/xsd/rest_representation.xsd?tags&#x3D;PUT) (required)
   * @param generateLabel Auto-generate label: true/false (optional, default to &quot;false&quot;)
   * @return Object
   */
  @RequestMapping(method = RequestMethod.PUT,
          value = "/{mmsId}/representations/{repId}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object putBibsMmsIdRepresentationsRepId(@PathVariable("mmsId") String mmsId, 
                                                  @PathVariable("repId") String repId,
                                                  @RequestBody Object body, 
                                                  @RequestParam("generate_label") String generateLabel);

  /**
   * Update Representation File
   * This web service updates a Digital Representation File.
   * @param mmsId The Bib Record ID. (required)
   * @param repId The Representation ID. (required)
   * @param fileId The File ID. (required)
   * @param body This method takes a Representation File object. See [here](/alma/apis/docs/xsd/rest_representation_file.xsd?tags&#x3D;PUT) (required)
   * @return Object
   */
  @RequestMapping(method = RequestMethod.PUT,
          value = "/{mmsId}/representations/{repId}/files/{fileId}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object putBibsMmsIdRepresentationsRepIdFilesFileId(@PathVariable("mmsId") String mmsId,
                                                             @PathVariable("repId") String repId,
                                                             @PathVariable("fileId") String fileId,
                                                             @RequestBody Object body);
}

package org.unidue.ub.libintel.almaconnector.clients.conf;

import feign.*;


public interface ImportProfilesApi {


  /**
   * Retrieve Import Profiles
   * This Web service returns a list of Import Profiles. In default mode, with no query parameters, all import profiles are returned.
   * @param type The Import Profile Type. Optional. Default is no Import Type. (optional, default to &quot;&quot;)
   * @param ieType The type of IE Entity created by this import. Optional. Default is to include all entities. (optional, default to &quot;ALL&quot;)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/conf/md-import-profiles?type={type}&ie_type={ieType}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1ConfMdImportProfiles(@Param("type") String type, @Param("ie_type") String ieType);

  /**
   * Retrieve Import Profile
   * This web service returns an Import profile given a Profile ID.
   * @param profileId The profile identifier. (required)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/conf/md-import-profiles/{profileId}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1ConfMdImportProfilesProfileId(@Param("profile_id") String profileId);

  /**
   * MD Import op - Deprecated
   * DEPRECATED - use the &#39;Submit Job&#39; API instead.   This web service runs an MD import according to a defined Import Profile. Supported for FTP, OAI and Digital.
   * @param profileId The profile identifier. (required)
   * @param op The operation to perform on the import profile. The operation op&#x3D;run is supported. (required)
   * @return Object
   */
  @RequestLine("POST /almaws/v1/conf/md-import-profiles/{profileId}?op={op}")
  @Headers({
    "Accept: application/json",
  })
  Object postAlmawsV1ConfMdImportProfilesProfileId(@Param("profile_id") String profileId, @Param("op") String op);
}

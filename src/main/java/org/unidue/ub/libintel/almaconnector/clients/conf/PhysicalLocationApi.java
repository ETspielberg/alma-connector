package org.unidue.ub.libintel.almaconnector.clients.conf;

import feign.*;


public interface PhysicalLocationApi {


  /**
   * Update a Location
   * This Web service updates a Physical Location.  
   * @param libraryCode The code of the library for which the location belongs to. (required)
   * @param locationCode The code of the location to be retrieved. (required)
   * @param body This method takes a location object. See [here](/alma/apis/docs/xsd/rest_location.xsd?tags&#x3D;PUT) (required)
   * @return Object
   */
  @RequestLine("PUT /almaws/v1/conf/libraries/{libraryCode}/locations/{locationCode}")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  Object putAlmawsV1ConfLibrariesLibraryCodeLocationsLocationCode(@Param("libraryCode") String libraryCode, @Param("locationCode") String locationCode, Object body);
}

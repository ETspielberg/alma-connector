package org.unidue.ub.libintel.almaconnector.clients.alma.conf;

import feign.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.almaconnector.clients.alma.AlmaFeignConfiguration;

@FeignClient(name = "organizationUnits", url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1/conf", configuration = AlmaFeignConfiguration.class)
@Service
public interface OrganizationUnitsApi {


  /**
   * Retrieve Departments
   * This API returns a list of all Departments configured for the Institution.
   * @param type Department type. The type DIGI (digitization) or ALL is supported. Default is ALL. (optional, default to &quot;ALL&quot;)
   * @param view Add optional parameter view&#x3D;brief to get a list of departments without operators. (optional, default to &quot;FULL&quot;)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/conf/departments?type={type}&view={view}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1ConfDepartments(@Param("type") String type, @Param("view") String view);

  /**
   * Retrieve Libraries
   * This API returns a list of all Libraries configured for the Institution.
   * @return Object
   */
  @RequestLine("GET /almaws/v1/conf/libraries")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1ConfLibraries();

  /**
   * Retrieve Library
   * This API retrieves details for a single Library.
   * @param libraryCode The code of the library. (required)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/conf/libraries/{libraryCode}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1ConfLibrariesLibraryCode(@Param("libraryCode") String libraryCode);

  /**
   * Retrieve Locations
   * This API returns a list of Physical Locations for a given Library.
   * @param libraryCode The code of the library for which the locations should be retrieved. (required)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/conf/libraries/{libraryCode}/locations")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1ConfLibrariesLibraryCodeLocations(@Param("libraryCode") String libraryCode);

  /**
   * Retrieve Location
   * This API returns a Physical Location.
   * @param libraryCode The code of the library for which the location belongs to. (required)
   * @param locationCode The code of the location to be retrieved. (required)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/conf/libraries/{libraryCode}/locations/{locationCode}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1ConfLibrariesLibraryCodeLocationsLocationCode(@Param("libraryCode") String libraryCode, @Param("locationCode") String locationCode);
}

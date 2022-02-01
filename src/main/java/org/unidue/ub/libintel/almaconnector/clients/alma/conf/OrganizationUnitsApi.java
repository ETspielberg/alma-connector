package org.unidue.ub.libintel.almaconnector.clients.alma.conf;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
  @RequestMapping(method= RequestMethod.GET,
          value="/departments",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getConfDepartments(@RequestParam("type") String type,
                                    @RequestParam("view") String view);

  /**
   * Retrieve Libraries
   * This API returns a list of all Libraries configured for the Institution.
   * @return Object
   */
  @RequestMapping(method=RequestMethod.GET,
          value="/libraries",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getConfLibraries();

  /**
   * Retrieve Library
   * This API retrieves details for a single Library.
   * @param libraryCode The code of the library. (required)
   * @return Object
   */
  @RequestMapping(method=RequestMethod.GET,
          value="/libraries/{libraryCode}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getConfLibrariesLibraryCode(@PathVariable("libraryCode") String libraryCode);

  /**
   * Retrieve Locations
   * This API returns a list of Physical Locations for a given Library.
   * @param libraryCode The code of the library for which the locations should be retrieved. (required)
   * @return Object
   */
  @RequestMapping(method=RequestMethod.GET,
          value="/libraries/{libraryCode}/locations",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getConfLibrariesLibraryCodeLocations(@PathVariable("libraryCode") String libraryCode);

  /**
   * Retrieve Location
   * This API returns a Physical Location.
   * @param libraryCode The code of the library for which the location belongs to. (required)
   * @param locationCode The code of the location to be retrieved. (required)
   * @return Object
   */
  @RequestMapping(method=RequestMethod.GET,
          value="/libraries/{libraryCode}/locations/{locationCode}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getConfLibrariesLibraryCodeLocationsLocationCode(@PathVariable("libraryCode") String libraryCode, 
                                                                  @PathVariable("locationCode") String locationCode);
}

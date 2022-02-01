package org.unidue.ub.libintel.almaconnector.clients.alma.conf;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.unidue.ub.libintel.almaconnector.clients.alma.AlmaFeignConfiguration;

@FeignClient(name = "general", url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1/conf", configuration = AlmaFeignConfiguration.class)
@Service
public interface GeneralApi {


  /**
   * Retrieve Code-table
   * This API returns all rows defined for a code-table.
   * @param codeTableName Code table name. (required)
   * @param lang Requested language. (optional, default to &quot;&quot;)
   * @return Object
   */
  @RequestMapping(method= RequestMethod.GET,
          value="/code-tables/{codeTableName}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getConfCodeTablesCodeTableName(@PathVariable("codeTableName") String codeTableName, @RequestParam("lang") String lang);

  /**
   * Retrieve General Configuration
   * This Web service returns the general configuration of the institution.
   * @return Object
   */
  @RequestMapping(method=RequestMethod.GET,
          value="/general",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getConfGeneral();

  /**
   * Retrieve Library Open Hours
   * This API returns a list of open days and hours for a given library.  Note that the library-hours do not necessarily reflect when the library doors are actually open, but rather start and end times that effect loan period.  This API is limited to one month of days from 1 year ago to 3 years ahead for a single request.
   * @param libraryCode The code for the institution from which the open hours should be retrieved. (required)
   * @param from From this Date (YYYY-MM-DD). Defaults to today. (optional, default to &quot;today&quot;)
   * @param to To this Date (YYYY-MM-DD). Defaults to the From Date plus one week. (optional, default to &quot;&quot;)
   * @return Object
   */
  @RequestMapping(method=RequestMethod.GET,
          value="/libraries/{libraryCode}/open-hours",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getConfLibrariesLibraryCodeOpenHours(@PathVariable("libraryCode") String libraryCode,
                                              @RequestParam("from") String from,
                                              @RequestParam("to") String to);

  /**
   * Retrieve Open Hours
   * This Web service returns the open-hours as configured in Alma.
   * @param scope This optional parameter specifies a library scope. Default will be institution, e.g. 01AAA_INST. (required)
   * @return Object
   */
  @RequestMapping(method=RequestMethod.GET,
          value="/open-hours",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getConfOpenHours(@RequestParam("scope") String scope);
}

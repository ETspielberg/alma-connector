package org.unidue.ub.libintel.almaconnector.clients.alma.bib;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.unidue.ub.libintel.almaconnector.clients.alma.AlmaFeignConfiguration;

@FeignClient(name = "test", url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1/bibs", configuration = AlmaFeignConfiguration.class)
@Service
public interface TestApi {


  /**
   * GET Inventory Test API
   * This API is used to test if the API key was configured correctly.
   * @return Object
   */
  @RequestMapping(method = RequestMethod.GET,
  value = "/test",
  produces = MediaType.APPLICATION_JSON_VALUE)
  Object getBibsTest();

  /**
   * POST Inventory Test API
   * This API is used to test if the API key was configured correctly, including read/write permissions.
   * @return Object
   */
  @RequestMapping(method = RequestMethod.POST,
  value = "/test",
  produces = MediaType.APPLICATION_JSON_VALUE,
  consumes = MediaType.APPLICATION_JSON_VALUE)
  Object postBibsTest();
}

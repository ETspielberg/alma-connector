package org.unidue.ub.libintel.almaconnector.clients.alma.bib;

import feign.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.almaconnector.clients.alma.AlmaFeignConfiguration;

@FeignClient(name = "test", url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1/bibs", configuration = AlmaFeignConfiguration.class)
@Service
public interface TestApi {


  /**
   * GET Inventory Test API
   * This API is used to test if the API key was configured correctly.
   * @return Object
   */
  @RequestLine("GET /almaws/v1/bibs/test")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1BibsTest();

  /**
   * POST Inventory Test API
   * This API is used to test if the API key was configured correctly, including read/write permissions.
   * @return Object
   */
  @RequestLine("POST /almaws/v1/bibs/test")
  @Headers({
    "Accept: application/json",
  })
  Object postAlmawsV1BibsTest();
}

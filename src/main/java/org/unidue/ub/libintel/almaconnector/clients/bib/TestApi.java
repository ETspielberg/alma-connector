package org.unidue.ub.libintel.almaconnector.clients.bib;

import feign.*;


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

package org.unidue.ub.libintel.almaconnector.clients.conf;

import feign.*;


public interface TestApi {


  /**
   * GET Conf Test API
   * This API is used to test if the API key was configured correctly.It returns a short XML (no schema available - the output is subject to changes) with the following structure:&lt;test&gt;GET - OK - institutionCode: 01ABC_INST&lt;/test&gt;
   * @return Object
   */
  @RequestLine("GET /almaws/v1/conf/test")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1ConfTest();

  /**
   * POST Conf Test API
   * This API is used to test if the API key was configured correctly, including read/write permissions.It returns a short XML (no schema available - the output is subject to changes) with the following structure:&lt;test&gt;POST - OK&lt;/test&gt;
   * @return Object
   */
  @RequestLine("POST /almaws/v1/conf/test")
  @Headers({
    "Accept: application/json",
  })
  Object postAlmawsV1ConfTest();
}

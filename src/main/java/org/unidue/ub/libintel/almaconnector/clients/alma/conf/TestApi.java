package org.unidue.ub.libintel.almaconnector.clients.alma.conf;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.unidue.ub.libintel.almaconnector.clients.alma.AlmaFeignConfiguration;

@FeignClient(name = "test", url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1/conf/test", configuration = AlmaFeignConfiguration.class)
@Service
public interface TestApi {


  /**
   * GET Conf Test API
   * This API is used to test if the API key was configured correctly.It returns a short XML (no schema available - the output is subject to changes) with the following structure:&lt;test&gt;GET - OK - institutionCode: 01ABC_INST&lt;/test&gt;
   * @return Object
   */
  @RequestMapping(method= RequestMethod.GET,
          value="",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getConfTest();

  /**
   * POST Conf Test API
   * This API is used to test if the API key was configured correctly, including read/write permissions.It returns a short XML (no schema available - the output is subject to changes) with the following structure:&lt;test&gt;POST - OK&lt;/test&gt;
   * @return Object
   */
  @RequestMapping(method=RequestMethod.POST,
          value="",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object postConfTest();
}

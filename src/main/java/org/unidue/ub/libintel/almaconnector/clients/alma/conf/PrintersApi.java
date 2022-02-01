package org.unidue.ub.libintel.almaconnector.clients.alma.conf;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.unidue.ub.libintel.almaconnector.clients.alma.AlmaFeignConfiguration;

@FeignClient(name = "printers", url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1/conf/printers", configuration = AlmaFeignConfiguration.class)
@Service
public interface PrintersApi {


  /**
   * Retrieve Printers
   * This API returns a list of Printers.
   * @param library Printer library code. Optional.  (optional, default to &quot;ALL&quot;)
   * @param printoutQueue Printer printout queue indication. Optional.  (optional, default to &quot;ALL&quot;)
   * @param name Printer Name. Optional.  (optional, default to &quot;ALL&quot;)
   * @param code Printer Code. Optional.  (optional, default to &quot;ALL&quot;)
   * @param limit Limits the number of results. Optional. Valid values are 0-100. Default value: 10. (optional)
   * @param offset Offset of the results returned. Optional. Default value: 0, which means that the first results will be returned. (optional)
   * @return Object
   */
  @RequestMapping(method= RequestMethod.GET,
          value="",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getConfPrinters(@RequestParam("library") String library,
                         @RequestParam("printout_queue") String printoutQueue,
                         @RequestParam("name") String name,
                         @RequestParam("code") String code,
                         @RequestParam("limit") Integer limit,
                         @RequestParam("offset") Integer offset);

  /**
   * Retrieve a Printer
   * This Web service returns a Printer given a Printer ID.
   * @param printerId  (required)
   * @return Object
   */
  @RequestMapping(method=RequestMethod.GET,
          value="/{printerId}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getConfPrintersPrinterId(@PathVariable("printerId") String printerId);
}

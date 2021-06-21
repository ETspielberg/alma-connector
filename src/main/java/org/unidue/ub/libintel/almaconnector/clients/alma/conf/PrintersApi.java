package org.unidue.ub.libintel.almaconnector.clients.alma.conf;

import feign.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
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
  @RequestLine("GET /almaws/v1/conf/printers?library={library}&printout_queue={printoutQueue}&name={name}&code={code}&limit={limit}&offset={offset}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1ConfPrinters(@Param("library") String library, @Param("printout_queue") String printoutQueue, @Param("name") String name, @Param("code") String code, @Param("limit") Integer limit, @Param("offset") Integer offset);

  /**
   * Retrieve a Printer
   * This Web service returns a Printer given a Printer ID.
   * @param printerId  (required)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/conf/printers/{printerId}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1ConfPrintersPrinterId(@Param("printer_id") String printerId);
}

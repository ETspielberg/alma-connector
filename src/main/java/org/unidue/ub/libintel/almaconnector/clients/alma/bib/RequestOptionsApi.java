package org.unidue.ub.libintel.almaconnector.clients.alma.bib;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.unidue.ub.libintel.almaconnector.clients.alma.AlmaFeignConfiguration;

@FeignClient(name = "requestOptions", url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1/bibs", configuration = AlmaFeignConfiguration.class)
@Service
public interface RequestOptionsApi {


  /**
   * Retrieve request options.
   * Retrieve the request options for this item.
   * @param mmsId The Bib Record ID. (required)
   * @param holdingId The Holding Record ID. (required)
   * @param itemPid The Item ID. (required)
   * @param userId The id of the user for which the request options will be calculated. Default value: GUEST (optional, default to &quot;GUEST&quot;)
   * @return Object
   */
  @RequestMapping(method = RequestMethod.GET,
          value = "/{mmsId}/holdings/{holdingId}/items/{itemPid}/request-options",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getBibsMmsIdHoldingsHoldingIdItemsItemPidRequestOptions(@PathVariable("mmsId") String mmsId,
                                                                         @PathVariable("holdingId") String holdingId,
                                                                         @PathVariable("itemPid") String itemPid,
                                                                         @RequestParam("user_id") String userId);


  /**
   * Retrieve request options.
   * Retrieve the request options for this item.
   * @param mmsId The Bib Record ID. (required)
   * @param userId The id of the user for which the request options will be calculated. Default value: GUEST (optional, default to &quot;GUEST&quot;)
   * @param considerDlr Include Display logic rules.  Default value: false (optional, default to &quot;false&quot;)
   * @return Object
   */
  @RequestMapping(method = RequestMethod.GET,
          value = "/{mmsId}/request-options",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getBibsMmsIdRequestOptions(@PathVariable("mmsId") String mmsId,
                                            @RequestParam("user_id") String userId,
                                            @RequestParam("consider_dlr") String considerDlr);
}

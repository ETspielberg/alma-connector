package org.unidue.ub.libintel.almaconnector.clients.alma.bib;

import feign.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
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
  @RequestLine("GET /almaws/v1/bibs/{mmsId}/holdings/{holdingId}/items/{itemPid}/request-options?user_id={userId}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1BibsMmsIdHoldingsHoldingIdItemsItemPidRequestOptions(@Param("mms_id") String mmsId, @Param("holding_id") String holdingId, @Param("item_pid") String itemPid, @Param("user_id") String userId);


  /**
   * Retrieve request options.
   * Retrieve the request options for this item.
   * @param mmsId The Bib Record ID. (required)
   * @param userId The id of the user for which the request options will be calculated. Default value: GUEST (optional, default to &quot;GUEST&quot;)
   * @param considerDlr Include Display logic rules.  Default value: false (optional, default to &quot;false&quot;)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/bibs/{mmsId}/request-options?user_id={userId}&consider_dlr={considerDlr}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1BibsMmsIdRequestOptions(@Param("mms_id") String mmsId, @Param("user_id") String userId, @Param("consider_dlr") String considerDlr);
}

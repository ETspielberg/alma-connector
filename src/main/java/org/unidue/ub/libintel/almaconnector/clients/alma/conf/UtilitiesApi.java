package org.unidue.ub.libintel.almaconnector.clients.alma.conf;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.unidue.ub.libintel.almaconnector.clients.alma.AlmaFeignConfiguration;

@FeignClient(name = "utilities", url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1/conf/utlilities", configuration = AlmaFeignConfiguration.class)
@Service
public interface UtilitiesApi {
  /**
   * Retrieve Fine Fee Report
   * This API returns a fine and fee report.
   * @param limit Limits the number of fees not transactions. Optional. Valid values are 0-100. Default value: 10. (optional)
   * @param offset Offset of the results returned. Optional. Default value: 0, which means that the first results will be returned. (optional)
   * @param transactionFromDate From this Date (YYYY-MM-DD). Defaults to today. (optional, default to &quot;today&quot;)
   * @param transactionToDate To this Date (YYYY-MM-DD). Defaults to from date. Limit of 7 days period. (optional, default to &quot;&quot;)
   * @param transactionType Add fine fee transaction type filter.  This defaults to both payment and waived. (optional, default to &quot;BOTH&quot;)
   * @param owner Add fine owner filter.  This is a library or the institution code. (optional, default to &quot;&quot;)
   * @param receivedByCircLibrary Add circulation library filter. May be used in conjunction with received_by_circ_desk to filter results. (optional, default to &quot;&quot;)
   * @param receivedByCircDesk Add circulation desk filter. Must be used in conjunction with received_by_circ_library to filter results. (optional, default to &quot;&quot;)
   * @return Object
   */
  @RequestMapping(method= RequestMethod.GET,
          value="",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getConfUtilitiesFeeTransactions(@RequestParam("limit") Integer limit,
                                                 @RequestParam("offset") Integer offset,
                                                 @RequestParam("transaction_from_date") String transactionFromDate,
                                                 @RequestParam("transaction_to_date") String transactionToDate,
                                                 @RequestParam("transaction_type") String transactionType,
                                                 @RequestParam("owner") String owner,
                                                 @RequestParam("received_by_circ_library") String receivedByCircLibrary,
                                                 @RequestParam("received_by_circ_desk") String receivedByCircDesk);

}

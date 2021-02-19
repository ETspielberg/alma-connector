package org.unidue.ub.libintel.almaconnector.clients.conf;

import feign.*;


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
  @RequestLine("GET /almaws/v1/conf/utilities/fee-transactions?limit={limit}&offset={offset}&transaction_from_date={transactionFromDate}&transaction_to_date={transactionToDate}&transaction_type={transactionType}&owner={owner}&received_by_circ_library={receivedByCircLibrary}&received_by_circ_desk={receivedByCircDesk}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1ConfUtilitiesFeeTransactions(@Param("limit") Integer limit, @Param("offset") Integer offset, @Param("transaction_from_date") String transactionFromDate, @Param("transaction_to_date") String transactionToDate, @Param("transaction_type") String transactionType, @Param("owner") String owner, @Param("received_by_circ_library") String receivedByCircLibrary, @Param("received_by_circ_desk") String receivedByCircDesk);

}

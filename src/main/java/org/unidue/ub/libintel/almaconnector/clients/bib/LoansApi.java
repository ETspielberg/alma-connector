package org.unidue.ub.libintel.almaconnector.clients.bib;

import feign.*;


public interface LoansApi {


  /**
   * Loan By Item information
   * This web service returns Item Loan by Item information.
   * @param mmsId The Bib Record ID. (required)
   * @param holdingId The Holding Record ID. (required)
   * @param itemId The Item ID. (required)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/bibs/{mmsId}/holdings/{holdingId}/items/{itemId}/loans")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1BibsMmsIdHoldingsHoldingIdItemsItemIdLoans(@Param("mms_id") String mmsId, @Param("holding_id") String holdingId, @Param("item_id") String itemId);

  /**
   * Retrieve Item Loan information
   * This web service returns Item Loan information.
   * @param mmsId The Bib Record ID. (required)
   * @param holdingId The Holding Record ID. (required)
   * @param itemPid The Item ID. (required)
   * @param loanId The Loan ID. (required)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/bibs/{mmsId}/holdings/{holdingId}/items/{itemPid}/loans/{loanId}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1BibsMmsIdHoldingsHoldingIdItemsItemPidLoansLoanId(@Param("mms_id") String mmsId, @Param("holding_id") String holdingId, @Param("item_pid") String itemPid, @Param("loan_id") String loanId);

  /**
   * Retrieve Bib Loan information
   * This web service returns Loan information for a Bib record.
   * @param mmsId The Bib Record ID. (required)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/bibs/{mmsId}/loans")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1BibsMmsIdLoans(@Param("mms_id") String mmsId);

  /**
   * Retrieve Bib Loan information for a Bib id and Loan id
   * This Web service retrieves loan information for a particular Bib id and Loan id.
   * @param mmsId The Bib Record ID. (required)
   * @param loanId The Loan ID. (required)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/bibs/{mmsId}/loans/{loanId}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1BibsMmsIdLoansLoanId(@Param("mms_id") String mmsId, @Param("loan_id") String loanId);

  /**
   * Create user loan
   * This web service loans an item to a user. The loan will be created according to the library&#39;s policy.
   * @param mmsId The Bib Record ID. (required)
   * @param holdingId The Holding Record ID. (required)
   * @param itemPid The Item ID. (required)
   * @param userId A unique identifier of the loaning user. Mandatory. (required)
   * @param body This method takes a Loan object. See [here](/alma/apis/docs/xsd/rest_item_loan.xsd?tags&#x3D;POST) (required)
   * @param userIdType The type of identifier that is being searched. Optional. If this is not provided, all unique identifier types are used. The values that can be used are any of the values in the User Identifier Type code table. (optional, default to &quot;all_unique&quot;)
   * @return Object
   */
  @RequestLine("POST /almaws/v1/bibs/{mmsId}/holdings/{holdingId}/items/{itemPid}/loans?user_id={userId}&user_id_type={userIdType}")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  Object postAlmawsV1BibsMmsIdHoldingsHoldingIdItemsItemPidLoans(@Param("mms_id") String mmsId, @Param("holding_id") String holdingId, @Param("item_pid") String itemPid, @Param("user_id") String userId, Object body, @Param("user_id_type") String userIdType);


  /**
   * Action on a loan
   * This Web service performs an action on a loan.
   * @param mmsId The Bib Record ID. (required)
   * @param holdingId The Holding Record ID. (required)
   * @param itemPid The Item ID. (required)
   * @param loanId The Loan ID. (required)
   * @param op Operation. Currently only op&#x3D;renew is supported (required)
   * @return Object
   */
  @RequestLine("POST /almaws/v1/bibs/{mmsId}/holdings/{holdingId}/items/{itemPid}/loans/{loanId}?op={op}")
  @Headers({
    "Accept: application/json",
  })
  Object postAlmawsV1BibsMmsIdHoldingsHoldingIdItemsItemPidLoansLoanId(@Param("mms_id") String mmsId, @Param("holding_id") String holdingId, @Param("item_pid") String itemPid, @Param("loan_id") String loanId, @Param("op") String op);


  /**
   * Change loan due date
   * This Web service changes a loan due date.
   * @param mmsId The Bib Record ID. (required)
   * @param holdingId The Holding Record ID. (required)
   * @param itemPid The Item ID. (required)
   * @param loanId The Loan ID. (required)
   * @param body This method takes an item loan object See [here](/alma/apis/docs/xsd/rest_item_loan.xsd?tags&#x3D;PUT) (required)
   * @return Object
   */
  @RequestLine("PUT /almaws/v1/bibs/{mmsId}/holdings/{holdingId}/items/{itemPid}/loans/{loanId}")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  Object putAlmawsV1BibsMmsIdHoldingsHoldingIdItemsItemPidLoansLoanId(@Param("mms_id") String mmsId, @Param("holding_id") String holdingId, @Param("item_pid") String itemPid, @Param("loan_id") String loanId, Object body);
}

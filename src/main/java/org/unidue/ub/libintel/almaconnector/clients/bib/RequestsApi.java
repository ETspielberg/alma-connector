package org.unidue.ub.libintel.almaconnector.clients.bib;

import feign.*;


public interface RequestsApi {


  /**
   * Cancel Request
   * This web service canceles a request.
   * @param mmsId The Bib Record ID. (required)
   * @param holdingId The Holding Record ID. (required)
   * @param itemPid The Item ID. (required)
   * @param requestId The Request ID. (required)
   * @param reason Code of the cancel reason. Must be a value from the code table &#39;RequestCancellationReasons&#39; (required)
   * @param note Note with additional information regarding the cancellation. (optional, default to &quot;&quot;)
   * @param notifyUser Boolean flag for notifying the requester of the cancellation (when relevant). Defaults to &#39;true&#39;. (optional, default to true)
   */
  @RequestLine("DELETE /almaws/v1/bibs/{mmsId}/holdings/{holdingId}/items/{itemPid}/requests/{requestId}?reason={reason}&note={note}&notify_user={notifyUser}")
  @Headers({
    "Accept: application/json",
  })
  void deleteAlmawsV1BibsMmsIdHoldingsHoldingIdItemsItemPidRequestsRequestId(@Param("mmsId") String mmsId, @Param("holdingId") String holdingId, @Param("itemPid") String itemPid, @Param("requestId") String requestId, @Param("reason") String reason, @Param("note") String note, @Param("notify_user") Boolean notifyUser);


  /**
   * Cancel Title Request
   * This web service cancels a request.
   * @param mmsId  (required)
   * @param requestId  (required)
   * @param reason Code of the cancel reason. Must be a value from the code table &#39;RequestCancellationReasons&#39; (required)
   * @param note Note with additional information regarding the cancellation. (optional, default to &quot;&quot;)
   * @param notifyUser Boolean flag for notifying the requester of the cancellation (when relevant). Defaults to &#39;true&#39;. (optional, default to true)
   */
  @RequestLine("DELETE /almaws/v1/bibs/{mmsId}/requests/{requestId}?reason={reason}&note={note}&notify_user={notifyUser}")
  @Headers({
    "Accept: application/json",
  })
  void deleteAlmawsV1BibsMmsIdRequestsRequestId(@Param("mmsId") String mmsId, @Param("requestId") String requestId, @Param("reason") String reason, @Param("note") String note, @Param("notify_user") Boolean notifyUser);


  /**
   * Retrieve booking availability for a Title
   * This web service returns list of periods in which specific title (MMS) is unavailable for booking.
   * @param mmsId The Bib Record ID. (required)
   * @param period The number of days/weeks/months to retrieve availability for. Mandatory. (required)
   * @param periodType The type of period of interest. Optional. Possible values: days, weeks, months. Default: days. (required)
   * @param userId A unique identifier for the user, for which the booking request is about to be done. Optional. If not supplied, the system will calculate a minimal availability. (optional, default to &quot;&quot;)
   * @param userIdType The type of identifier that is being searched. Optional. If this is not provided, all unique identifier types are used. The values that can be used are any of the values in the User Identifier Type code table. (optional, default to &quot;all_unique&quot;)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/bibs/{mmsId}/booking-availability?period={period}&period_type={periodType}&user_id={userId}&user_id_type={userIdType}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1BibsMmsIdBookingAvailability(@Param("mms_id") String mmsId, @Param("period") Integer period, @Param("period_type") String periodType, @Param("user_id") String userId, @Param("user_id_type") String userIdType);


  /**
   * Retrieve User Requests per Item
   * This web service returns a list of requests per the given item (using item_id).
   * @param mmsId The Bib Record ID. (required)
   * @param holdingId The Holding Bib Record ID. (required)
   * @param itemId The item ID. (required)
   * @param requestType Filter results by request type. Optional. Possible values: HOLD, DIGITIZATION, BOOKING. If not supplied, all request types will be returned. (optional, default to &quot;all_types&quot;)
   * @param status Active or history request status . The default is active. (optional, default to &quot;active&quot;)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/bibs/{mmsId}/holdings/{holdingId}/items/{itemId}/requests?request_type={requestType}&status={status}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1BibsMmsIdHoldingsHoldingIdItemsItemIdRequests(@Param("mms_id") String mmsId, @Param("holding_id") String holdingId, @Param("item_id") String itemId, @Param("request_type") String requestType, @Param("status") String status);


  /**
   * Retrieve User Item Request
   * This web service return a request per the given item and request ID.
   * @param mmsId The Bib Record ID. (required)
   * @param holdingId The Holding Bib Record ID. (required)
   * @param itemId The item ID. (required)
   * @param requestId The Request ID. (required)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/bibs/{mmsId}/holdings/{holdingId}/items/{itemId}/requests/{requestId}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1BibsMmsIdHoldingsHoldingIdItemsItemIdRequestsRequestId(@Param("mms_id") String mmsId, @Param("holding_id") String holdingId, @Param("item_id") String itemId, @Param("request_id") String requestId);

  /**
   * Retrieve booking availability for an Item
   * This web service returns list of periods in which specific item is unavailable for booking.
   * @param mmsId The Bib Record ID. (required)
   * @param holdingId The Holding Record ID. (required)
   * @param itemPid The Item ID. (required)
   * @param period The number of days/weeks/months to retrieve availability for. Mandatory. (required)
   * @param periodType The type of period of interest. Optional. Possible values: days, weeks, months. Default: days. (required)
   * @param userId A unique identifier for the user, for which the booking request is about to be done. Optional. If not supplied, the system will calculate a minimal availability. (optional, default to &quot;&quot;)
   * @param userIdType The type of identifier that is being searched. Optional. If this is not provided, all unique identifier types are used. The values that can be used are any of the values in the User Identifier Type code table. (optional, default to &quot;all_unique&quot;)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/bibs/{mmsId}/holdings/{holdingId}/items/{itemPid}/booking-availability?period={period}&period_type={periodType}&user_id={userId}&user_id_type={userIdType}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1BibsMmsIdHoldingsHoldingIdItemsItemPidBookingAvailability(@Param("mms_id") String mmsId, @Param("holding_id") String holdingId, @Param("item_pid") String itemPid, @Param("period") Integer period, @Param("period_type") String periodType, @Param("user_id") String userId, @Param("user_id_type") String userIdType);


  /**
   * Retrieve User Requests per Bib
   * This web service returns a list of requests per the given bib (using mms_id).
   * @param mmsId The Bib Record ID. (required)
   * @param requestType Filter results by request type. Optional. Possible values: HOLD, DIGITIZATION, BOOKING, MOVE, WORK_ORDER. If not supplied, all request types will be returned. (optional, default to &quot;all_types&quot;)
   * @param status Active or history requests status. Default is active. (optional, default to &quot;active&quot;)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/bibs/{mmsId}/requests?request_type={requestType}&status={status}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1BibsMmsIdRequests(@Param("mms_id") String mmsId, @Param("request_type") String requestType, @Param("status") String status);

  /**
   * Retrieve User Title Request
   * This web service returns a request per the given bib by the request ID.
   * @param mmsId The Bib Record ID. (required)
   * @param requestId The Request ID. (required)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/bibs/{mmsId}/requests/{requestId}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1BibsMmsIdRequestsRequestId(@Param("mms_id") String mmsId, @Param("request_id") String requestId);

  /**
   * Create request for an Item
   * This web service creates a request for a library resource. The request can be for a physical item (request types: hold, booking), or a request for digitizing a file (request type: digitization). Currently it is not possible to create a move or work order request.
   * @param mmsId The Bib Record ID. (required)
   * @param holdingId The Holding Record ID. (required)
   * @param itemPid The  requested Item ID. (required)
   * @param body This method takes a Request object. See [here](/alma/apis/docs/xsd/rest_user_request.xsd?tags&#x3D;POST) (required)
   * @param userId A unique identifier for the user. For a library level digitization request, leave empty. (optional, default to &quot;&quot;)
   * @param userIdType The type of identifier that is being searched. Optional. If this is not provided, all unique identifier types are used. The values that can be used are any of the values in the User Identifier Type code table. (optional, default to &quot;all_unique&quot;)
   * @return Object
   */
  @RequestLine("POST /almaws/v1/bibs/{mmsId}/holdings/{holdingId}/items/{itemPid}/requests?user_id={userId}&user_id_type={userIdType}")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  Object postAlmawsV1BibsMmsIdHoldingsHoldingIdItemsItemPidRequests(@Param("mms_id") String mmsId, @Param("holding_id") String holdingId, @Param("item_pid") String itemPid, Object body, @Param("user_id") String userId, @Param("user_id_type") String userIdType);


  /**
   * Action on a request - Item
   * This API performs an action on a request. Currently supported: moving digitization requests to their next step.
   * @param mmsId The Bib Record ID. (required)
   * @param holdingId The Holding Record ID. (required)
   * @param itemPid The Item ID. (required)
   * @param requestId The request ID. (required)
   * @param op The operation to be performed on the request. Mandatory. Currently only next_step is supported. (optional, default to &quot;&quot;)
   * @param releaseItem Boolean flag for indicating whether to release the item from the request (optional, default to &quot;false&quot;)
   * @return Object
   */
  @RequestLine("POST /almaws/v1/bibs/{mmsId}/holdings/{holdingId}/items/{itemPid}/requests/{requestId}?op={op}&release_item={releaseItem}")
  @Headers({
    "Accept: application/json",
  })
  Object postAlmawsV1BibsMmsIdHoldingsHoldingIdItemsItemPidRequestsRequestId(@Param("mms_id") String mmsId, @Param("holding_id") String holdingId, @Param("item_pid") String itemPid, @Param("request_id") String requestId, @Param("op") String op, @Param("release_item") String releaseItem);


  /**
   * Create request for a Title
   * This web service creates a request for a library resource. The request can be for a physical item (request types: hold, booking), or a request for digitizing a file (request type: digitization). Currently it is not possible to create a move or work order request.
   * @param mmsId The Bib Record ID. (required)
   * @param body This method takes a Request object. See [here](/alma/apis/docs/xsd/rest_user_request.xsd?tags&#x3D;POST) (required)
   * @param userId A unique identifier for the user. For a library level digitization request, leave empty. (optional, default to &quot;&quot;)
   * @param userIdType The type of identifier that is being searched. Optional. If this is not provided, all unique identifier types are used. The values that can be used are any of the values in the User Identifier Type code table. (optional, default to &quot;all_unique&quot;)
   * @return Object
   */
  @RequestLine("POST /almaws/v1/bibs/{mmsId}/requests?user_id={userId}&user_id_type={userIdType}")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  Object postAlmawsV1BibsMmsIdRequests(@Param("mms_id") String mmsId, Object body, @Param("user_id") String userId, @Param("user_id_type") String userIdType);


  /**
   * Action on a request - Title
   * This API performs an action on a request. Currently supported: moving digitization requests to their next step.
   * @param mmsId The Bib Record ID. (required)
   * @param requestId The Request ID. (required)
   * @param op The operation to be performed on the request. Mandatory. Currently only next_step is supported. (optional, default to &quot;&quot;)
   * @param releaseItem Boolean flag for indicating whether to release the item from the request (optional, default to &quot;false&quot;)
   * @return Object
   */
  @RequestLine("POST /almaws/v1/bibs/{mmsId}/requests/{requestId}?op={op}&release_item={releaseItem}")
  @Headers({
    "Accept: application/json",
  })
  Object postAlmawsV1BibsMmsIdRequestsRequestId(@Param("mms_id") String mmsId, @Param("request_id") String requestId, @Param("op") String op, @Param("release_item") String releaseItem);


  /**
   * Update Item Request
   * This web service updates a request for a library resource.
   * @param mmsId The Bib Record ID. (required)
   * @param holdingId The Holding Record ID. (required)
   * @param itemPid The Item ID. (required)
   * @param requestId The request ID. (required)
   * @param body This method takes a User-Request object. See [here](/alma/apis/docs/xsd/rest_user_request.xsd?tags&#x3D;PUT) (required)
   * @return Object
   */
  @RequestLine("PUT /almaws/v1/bibs/{mmsId}/holdings/{holdingId}/items/{itemPid}/requests/{requestId}")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  Object putAlmawsV1BibsMmsIdHoldingsHoldingIdItemsItemPidRequestsRequestId(@Param("mms_id") String mmsId, @Param("holding_id") String holdingId, @Param("item_pid") String itemPid, @Param("request_id") String requestId, Object body);

  /**
   * Update Title Request
   * This web service updates a request for a library resource.
   * @param mmsId The Bib Record ID. (required)
   * @param requestId The Request ID. (required)
   * @param body This method takes a UserRequest object. See [here](/alma/apis/docs/xsd/rest_user_request.xsd?tags&#x3D;PUT) (required)
   * @return Object
   */
  @RequestLine("PUT /almaws/v1/bibs/{mmsId}/requests/{requestId}")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  Object putAlmawsV1BibsMmsIdRequestsRequestId(@Param("mms_id") String mmsId, @Param("request_id") String requestId, Object body);
}

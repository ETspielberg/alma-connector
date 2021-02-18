package org.unidue.ub.libintel.almaconnector.clients.bib;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.unidue.ub.alma.shared.bibs.*;

import org.springframework.http.MediaType;
import java.util.List;

@FeignClient(name = "invoices", url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1/bibs", configuration = CatalogFeignConfiguration.class)
@Service
public interface AlmaCatalogApiClient {


  /**
   * Delete Bib Record
   * This web service deletes a Bib Record.  For more information regarding the various options supported for this API see [here](https://developers.exlibrisgroup.com/blog/cataloging-APIs-enhancements).
   * @param mmsId The Bib Record ID. (required)
   * @param override Override the warnings and delete the Bib Record. Optional. The default is to not override (false). (optional, default to &quot;false&quot;)
   * @param catalogerLevel Cataloger level of the user deleting the record. (optional, default to &quot;&quot;)
   */
  @RequestMapping(method= RequestMethod.DELETE, value="/{mmsId}?override={override}&cataloger_level={catalogerLevel}")
  void deleteBibsMmsId(@RequestParam("mms_id") String mmsId, @RequestParam("override") String override, @RequestParam("cataloger_level") String catalogerLevel);

  /**
   * Delete Holdings Record
   * This web service deletes a Holdings Record.
   * @param mmsId The Bib Record ID. (required)
   * @param holdingId The Holding Record ID. (required)
   * @param bib Method for handling a Bib record left without any holdings: retain, delete or suppress. Optional. By default: retain. (optional, default to &quot;retain&quot;)
   */
  @RequestMapping(method= RequestMethod.DELETE, value="/{mmsId}/holdings/{holdingId}?bib={bib}")
  void deleteBibsMmsIdHoldingsHoldingId(@RequestParam("mms_id") String mmsId, @RequestParam("holding_id") String holdingId, @RequestParam("bib") String bib);

  /**
   * Withdraw Item
   * This web service withdraws an item.
   * @param mmsId The Bib Record ID. (required)
   * @param holdingId The Holding Record ID. (required)
   * @param itemPid The Item ID. (required)
   * @param override Indication whether the item should be deleted even if warnings exist. Optional. By default: false. (optional, default to &quot;false&quot;)
   * @param holdings Method for handling a Holdings record left without any items: retain, delete or suppress. Optional. By default: retain. (optional, default to &quot;retain&quot;)
   */
  @RequestMapping(method= RequestMethod.DELETE, value="/{mmsId}/holdings/{holdingId}/items/{itemPid}?override={override}&holdings={holdings}")
  void deleteBibsMmsIdHoldingsHoldingIdItemsItemPid(@RequestParam("mms_id") String mmsId, @RequestParam("holding_id") String holdingId, @RequestParam("item_pid") String itemPid, @RequestParam("override") String override, @RequestParam("holdings") String holdings);


  /**
   * Delete Portfolio for a Bib
   * This web service deletes a Portfolio for a Bib.
   * @param mmsId The Bib Record ID. (required)
   * @param portfolioId Unique ID of the electronic portfolio. (required)
   * @param bib Method for handling a bib left without any inventory: retain, suppress or delete. Optional. By default: delete. (optional, default to &quot;retain&quot;)
   */
  @RequestMapping(method= RequestMethod.DELETE, value="/{mmsId}/portfolios/{portfolioId}?bib={bib}")
  void deleteBibsMmsIdPortfoliosPortfolioId(@RequestParam("mms_id") String mmsId, @RequestParam("portfolio_id") String portfolioId, @RequestParam("bib") String bib);


  /**
   * Retrieve Bibs
   * This web service returns Bib records in an XML format from a list of Bib IDs submitted in a parameter.     To include physical inventory information use expand&#x3D;p_avail:   AVA field is added per holding record (related to the input mms-id or to a related Bib records) as following:   $$a - Institution code, $$b - Library code, $$c - Location display name, $$h - Campus,   $$d - Call number, $$e - Availability (such as available, unavailable, or check_holdings),   $$j - Location code, $$k - Call number type, $$f - total items, $$g - non available items,   $$v - Calculated summary information,   $$p - priority, $$0 - Bib record ID, $$8 - Holdings ID (of items on permanent location only), $$t - Holdings Information, $$q - library name.   AVA is added also for items in temporary location. For such items, $$8 (holding id) will not be added.  Note: When using the API against a NZ Institution AVA fields will be retrieved for each member which has holdings.    To include digital inventory information use: expand&#x3D;d_avail:   MARC - AVD field is added per Representations, as following:   $$a - Institution code, $$b - Representations ID, $$c - REPRESENTATION/REMOTE_REPRESENTATION, $$d - Repository Name, $$e - Label, $$f - Public Note, $$h - Full Text Link, $$r - IED.   Dublin Core - A dc:identifier field with a delivery URL is added per representation.  MODS - A location/url field with a delivery URL is added per representation.    To include electronic inventory information use: expand&#x3D;e_avail:   AVE field is added per portfolio, as following:   $$l -library code, $$m - Collection name, $$n - Public note, $$u - link to the bibliographic record&#39;s services page,   $$s - coverage statement (as displayed in Primo&#39;s ViewIt mashup), $$t - Interface name.   $$8 - portfolio pid, $$c - collection identifier for the electronic resource, $$e - activation status.   $$i - Available for institution, $$d - Available for library, $$b - Available for campus.   Note:  $$u will be created based on a Customer Parameter in the Customer Parameters mapping table (module: general): publishing_base_url.  Note: When using the API against a NZ Institution AVE fields will also be retrieved for each member which has portfolios, including $$a with the Institution code and $$0 with the mms-id.    Note: For Dublin Core records the expand parameter does not create an addition AVD field. Instead it creates a dc:identifier field with a delivery URL.  Note:The bibliographic record retrieved from Alma is enriched with additional identifiers.   The MMS ID of the Network Zone and the Alma Community Zone ID are added to the record   in additional 035 marc fields. The Community Zone ID is added with the prefix (EXLCZ)   while the Network Zone ID is added with the prefix (EXLNZ-network_code). The local   MMS ID is in the 001 marc field. These additional shared IDs can be used for better   identification of a common record. The local MMS ID should be used when there is a need to call   an API in the institution for the record.
   * @param mmsId A list of Bib Record IDs (for example: 99939650000541,99939680000541) from 1 to the limit of 100 (optional, default to &quot;&quot;)
   * @param ieId IE identifier (IEP, IED etc.) (optional, default to &quot;&quot;)
   * @param holdingsId Holdings ID (optional, default to &quot;&quot;)
   * @param representationId Representation ID (optional, default to &quot;&quot;)
   * @param nzMmsId Network Zone ID (optional, default to &quot;&quot;)
   * @param czMmsId Community Zone ID (optional, default to &quot;&quot;)
   * @param view Use view&#x3D;brief to retrieve without the full record. (optional, default to &quot;full&quot;)
   * @param expand This parameter allows for expanding the bibliographic record with additional information:   p_avail - Expand physical inventory information.   e_avail - Expand electronic inventory information.   d_avail - Expand digital inventory information.   requests - Expand total number of title requests.   To use more than one, use a comma separator. (optional, default to &quot;None&quot;)
   * @param otherSystemId An Other System Id. An additional ID stored as part of the record&#39;s network numbers. Optional. (optional, default to &quot;&quot;)
   * @return Object
   */
  @RequestMapping(method= RequestMethod.GET, value="/?mms_id={mmsId}&ie_id={ieId}&holdings_id={holdingsId}&representation_id={representationId}&nz_mms_id={nzMmsId}&cz_mms_id={czMmsId}&view={view}&expand={expand}&other_system_id={otherSystemId}")
  List<Bib> getBibs(@RequestParam("mms_id") String mmsId, @RequestParam("ie_id") String ieId, @RequestParam("holdings_id") String holdingsId, @RequestParam("representation_id") String representationId, @RequestParam("nz_mms_id") String nzMmsId, @RequestParam("cz_mms_id") String czMmsId, @RequestParam("view") String view, @RequestParam("expand") String expand, @RequestParam("other_system_id") String otherSystemId);


  /**
   * Retrieve Bib
   * This web service returns a Bib record in an XML format.     To include physical inventory information use expand&#x3D;p_avail:   AVA field is added per holding record (related to the input mms-id or to a related Bib records) as following:   $$a - Institution code, $$b - Library code, $$c - Location display name, $$h - Campus,   $$d - Call number, $$e - Availability (such as available, unavailable, or check_holdings),   $$j - Location code, $$k - Call number type, $$f - total items, $$g - non available items,   $$v - Calculated summary information,   $$p - priority, $$0 - Bib record ID, $$8 - Holdings ID (of items on permanent location only), $$t - Holdings Information, $$q - library name.   AVA is added also for items in temporary location. For such items, $$8 (holding id) will not be added.  Note: When using the API against a NZ Institution AVA fields will be retrieved for each member which has holdings.    To include digital inventory information use: expand&#x3D;d_avail:   MARC - AVD field is added per Representations, as following:   $$a - Institution code, $$b - Representations ID, $$c - REPRESENTATION/REMOTE_REPRESENTATION, $$d - Repository Name, $$e - Label, $$f - Public Note, $$h - Full Text Link, $$r - IED.   Dublin Core - A dc:identifier field with a delivery URL is added per representation.  MODS - A location/url field with a delivery URL is added per representation.    To include electronic inventory information use: expand&#x3D;e_avail:   AVE field is added per portfolio, as following:   $$l -library code, $$m - Collection name, $$n - Public note, $$u - link to the bibliographic record&#39;s services page,   $$s - coverage statement (as displayed in Primo&#39;s ViewIt mashup), $$t - Interface name.   $$8 - portfolio pid, $$c - collection identifier for the electronic resource, $$e - activation status.   $$i - Available for institution, $$d - Available for library, $$b - Available for campus.   Note:  $$u will be created based on a Customer Parameter in the Customer Parameters mapping table (module: general): publishing_base_url.  Note: When using the API against a NZ Institution AVE fields will also be retrieved for each member which has portfolios, including $$a with the Institution code and $$0 with the mms-id.    Note: For Dublin Core records the expand parameter does not create an addition AVD field. Instead it creates a dc:identifier field with a delivery URL.  Note:The bibliographic record retrieved from Alma is enriched with additional identifiers.   The MMS ID of the Network Zone and the Alma Community Zone ID are added to the record   in additional 035 marc fields. The Community Zone ID is added with the prefix (EXLCZ)   while the Network Zone ID is added with the prefix (EXLNZ-network_code). The local   MMS ID is in the 001 marc field. These additional shared IDs can be used for better   identification of a common record. The local MMS ID should be used when there is a need to call   an API in the institution for the record.
   * @param mmsId The Bib Record ID. (required)
   * @param view Use view&#x3D;brief to retrieve without the full record. Use view&#x3D;local_fields to retrieve only local fields for an IZ record linked to an NZ record. (optional, default to &quot;full&quot;)
   * @param expand This parameter allows for expanding the bibliographic record with additional information:   p_avail - Expand physical inventory information.   e_avail - Expand electronic inventory information.   d_avail - Expand digital inventory information.   requests - Expand total number of title requests.   To use more than one, use a comma separator. (optional, default to &quot;None&quot;)
   * @return Object
   */
  @RequestMapping(method= RequestMethod.GET, value="/{mmsId}?view={view}&expand={expand}", produces = MediaType.APPLICATION_XML_VALUE)
  Bib getBibsMmsId(@RequestParam("mms_id") String mmsId, @RequestParam("view") String view, @RequestParam("expand") String expand);


  /**
   * Retrieve Holdings list
   * This web service returns list of holding records for a given MMS.
   * @param mmsId The Bib Record ID. (required)
   * @return Object
   */
  @RequestMapping(method= RequestMethod.GET, value="/{mmsId}/holdings")
  List<HoldingData> getBibsMmsIdHoldings(@RequestParam("mms_id") String mmsId);

  /**
   * Retrieve Holdings Record
   * This web service returns a Holdings Record. In order to use this service, authentication must be done by a user that has the &#39;API Fulfillment Read&#39; role. Please note that the holding record is returned in MARC XML format, therefore it is not recommended to use this service with JSON format.
   * @param mmsId The Bib Record ID. (required)
   * @param holdingId The Holding Record ID. (required)
   * @return Object
   */
  @RequestMapping(method= RequestMethod.GET, value="/{mmsId}/holdings/{holdingId}")
  HoldingWithRecord getBibsMmsIdHoldingsHoldingId(@PathVariable String mmsId, @PathVariable String holdingId);

  /**
   * Retrieve Items list
   * This web service returns a list of Items.    [info!Note: It is possible to retrieve all the items under a bib record using &#39;ALL&#39; in the holding_id path parameter.]
   * @param mmsId The Bib Record ID. (required)
   * @param holdingId The Holding Record ID. May be ALL to retrieve all holdings for a Bib. (required)
   * @param limit Limits the number of results. Optional. Valid values are 0-100. Default value: 10. (optional)
   * @param offset Offset of the results returned. Optional. Default value: 0, which means that the first results will be returned. (optional)
   * @param expand Parameter for enhancing result with additional information. Currently supported: due_date_policy, due_date (optional, default to &quot;&quot;)
   * @param userId The id of the user for which the discovery information will be calculated. (optional, default to &quot;&quot;)
   * @param currentLibrary The current library for the item. (optional, default to &quot;&quot;)
   * @param currentLocation The current location of the item. (optional, default to &quot;&quot;)
   * @param q Search from enum_a, enum_b, chron_i, chron_j, and description. (see [Brief Search](https://developers.exlibrisgroup.com/blog/How-we-re-building-APIs-at-Ex-Libris#BriefSearch)) (optional, default to &quot;&quot;)
   * @param orderBy The sort order. Ordering may be selected by chron_i, description, enum_a, enum_b, enum_c, library, location or temporary_location or receive_date. There is no default. (optional, default to &quot;none&quot;)
   * @param direction The sort direction of desc (default) or asc. (optional, default to &quot;desc&quot;)
   * @param createDateFrom Retrieve items with create date after this Date (YYYY-MM-DD). Optional. (optional, default to &quot;&quot;)
   * @param createDateTo Retrieve items with create date before this Date (YYYY-MM-DD). Optional. (optional, default to &quot;&quot;)
   * @param modifyDateFrom Retrieve items with modify date after this Date (YYYY-MM-DD). Optional. (optional, default to &quot;&quot;)
   * @param modifyDateTo Retrieve items with modify date before this Date (YYYY-MM-DD). Optional. (optional, default to &quot;&quot;)
   * @param receiveDateFrom Retrieve items with receive date starting with this Date (YYYY-MM-DD). Optional. (optional, default to &quot;&quot;)
   * @param receiveDateTo Retrieve items with receive date until this Date (YYYY-MM-DD). Optional. (optional, default to &quot;&quot;)
   * @param expectedReceiveDateFrom Retrieve items with expected receive date starting with this Date (YYYY-MM-DD). Optional. (optional, default to &quot;&quot;)
   * @param expectedReceiveDateTo Retrieve items with expected receive date until this Date (YYYY-MM-DD). Optional. (optional, default to &quot;&quot;)
   * @param view Special view of an item object. Optional. Currently supported: label - adds fields relevant for label printing. (optional, default to &quot;brief&quot;)
   * @return Object
   */
  @RequestMapping(method= RequestMethod.GET, value="/{mmsId}/holdings/{holdingId}/items?limit={limit}&offset={offset}&expand={expand}&user_id={userId}&current_library={currentLibrary}&current_location={currentLocation}&q={q}&order_by={orderBy}&direction={direction}&create_date_from={createDateFrom}&create_date_to={createDateTo}&modify_date_from={modifyDateFrom}&modify_date_to={modifyDateTo}&receive_date_from={receiveDateFrom}&receive_date_to={receiveDateTo}&expected_receive_date_from={expectedReceiveDateFrom}&expected_receive_date_to={expectedReceiveDateTo}&view={view}\"")
  List<Item> getBibsMmsIdHoldingsHoldingIdItems(@RequestParam("mms_id") String mmsId, @RequestParam("holding_id") String holdingId, @RequestParam("limit") Integer limit, @RequestParam("offset") Integer offset, @RequestParam("expand") String expand, @RequestParam("user_id") String userId, @RequestParam("current_library") String currentLibrary, @RequestParam("current_location") String currentLocation, @RequestParam("q") String q, @RequestParam("order_by") String orderBy, @RequestParam("direction") String direction, @RequestParam("create_date_from") String createDateFrom, @RequestParam("create_date_to") String createDateTo, @RequestParam("modify_date_from") String modifyDateFrom, @RequestParam("modify_date_to") String modifyDateTo, @RequestParam("receive_date_from") String receiveDateFrom, @RequestParam("receive_date_to") String receiveDateTo, @RequestParam("expected_receive_date_from") String expectedReceiveDateFrom, @RequestParam("expected_receive_date_to") String expectedReceiveDateTo, @RequestParam("view") String view);


  /**
   * Retrieve Item and label printing information
   * This web service returns Item information.     [info!Note: It is also possible to retrieve item information by barcode using: GET /almaws/v1/items?item_barcode&#x3D;{item_barcode}. Calling this shorthand URL will return an HTTP 302 redirect response leading to a URL with the structure documented here.]
   * @param mmsId The Bib Record ID. (required)
   * @param holdingId The Holding Record ID. (required)
   * @param itemPid The Item ID. (required)
   * @param view Special view of Item object. Optional. Currently supported: label - adds fields relevant for label printing. (optional, default to &quot;brief&quot;)
   * @param expand Parameter for enhancing result with additional information. Currently supported: due_date_policy, due_date (optional, default to &quot;&quot;)
   * @param userId The id of the user which the due_date_policy expand will be calculated for. Default: GUEST. (optional, default to &quot;&quot;)
   * @return Object
   */
  @RequestMapping(method= RequestMethod.GET, value="/{mms_id}/holdings/{holding_id}/items/{item_pid}?view={view}&expand={expand}&user_id={user_id}")
  Item getBibsMmsIdHoldingsHoldingIdItemsItemPid(@RequestHeader("Accept") String accept, @RequestParam("mms_id") String mmsId, @RequestParam("holding_id") String holdingId, @RequestParam("item_pid") String itemPid, @RequestParam("view") String view, @RequestParam("expand") String expand, @RequestParam("user_id") String userId);


  /**
   * Retrieve Portfolios list
   * This web service returns Portfolios for a Bib.
   * @param mmsId The Bib Record ID. (required)
   * @param limit Limits the number of results. Optional. Valid values are 0-100. Default value: 10. (optional)
   * @param offset Offset of the results returned. Optional. Default value: 0, which means that the first results will be returned. (optional)
   * @return Object
   */
  @RequestMapping(method= RequestMethod.GET, value="/{mmsId}/portfolios?limit={limit}&offset={offset}")
  List<Portfolio> getBibsMmsIdPortfolios(@RequestParam("mms_id") String mmsId, @RequestParam("limit") Integer limit, @RequestParam("offset") Integer offset);


  /**
   * Retrieve Portfolio
   * This web service returns a Portfolio for a Bib ID and an Electronic Portfolio ID.
   * @param mmsId The Bib Record ID. (required)
   * @param portfolioId Unique ID of the electronic portfolio. (required)
   * @return Object
   */
  @RequestMapping(method= RequestMethod.GET, value="/{mmsId}/portfolios/{portfolioId}")
  Portfolio getBibsMmsIdPortfoliosPortfolioId(@RequestParam("mms_id") String mmsId, @RequestParam("portfolio_id") String portfolioId);

  /**
   * Create record
   * These web services create a new Bib record, and also allows creation of a local record for an NZ or a CZ record.   Note: JSON is not supported for this API.  For more information regarding the various options supported for this API see [here](https://developers.exlibrisgroup.com/blog/cataloging-APIs-enhancements).
   * @param body This method takes a Bib object. When creating linked record send an empty Bib object: &lt;bib/&gt; See [here](/alma/apis/docs/xsd/rest_bib.xsd?tags&#x3D;POST) (required)
   * @param fromNzMmsId The MMS_ID of the Network-Zone record. Leave empty when creating a regular local record. (optional, default to &quot;&quot;)
   * @param fromCzMmsId The MMS_ID of the Community-Zone record. Leave empty when creating a regular local record. (optional, default to &quot;&quot;)
   * @param normalization The id of the normalization profile to run. (optional, default to &quot;&quot;)
   * @param validate Indicating whether to check for errors. Default: false. (optional, default to &quot;false&quot;)
   * @param overrideWarning Indicating whether to ignore warnings. Default: true (record will be saved and the warnings will be added to the API output). (optional, default to &quot;true&quot;)
   * @param checkMatch Indicating whether to check for a match. Default: false (record will be saved despite possible match). (optional, default to &quot;false&quot;)
   * @return Object
   */
  @RequestMapping(method= RequestMethod.POST, value="/?from_nz_mms_id={fromNzMmsId}&from_cz_mms_id={fromCzMmsId}&normalization={normalization}&validate={validate}&override_warning={overrideWarning}&check_match={checkMatch}", consumes = MediaType.APPLICATION_XML_VALUE)
  BibWithRecord postBibs(Object body, @RequestParam("from_nz_mms_id") String fromNzMmsId, @RequestParam("from_cz_mms_id") String fromCzMmsId, @RequestParam("normalization") String normalization, @RequestParam("validate") String validate, @RequestParam("override_warning") String overrideWarning, @RequestParam("check_match") String checkMatch);


  /**
   * Operate on record
   * This web service performs an operation on a Bib record.  Currently, the supported operation is to unlink from NZ. Note: JSON is not supported for this API.
   * @param mmsId The Bib Record ID. (required)
   * @param op The operation that is to be performed. Currently, only option is: unlink_from_nz. (required)
   * @param body This method takes a Bib object. See [here](/alma/apis/docs/xsd/rest_bib.xsd?tags&#x3D;POST) (required)
   * @return Object
   */
  @RequestMapping(method= RequestMethod.POST, value="/{mmsId}?op={op}", consumes = MediaType.APPLICATION_XML_VALUE)
  BibWithRecord postBibsMmsId(@RequestParam("mms_id") String mmsId, @RequestParam("op") String op, Object body);


  /**
   * Create holding record
   * This web service creates a new holding record.   Note: JSON is not supported for this API.
   * @param mmsId The Bib Record ID. (required)
   * @param body This method takes a Holding object. See [here](/alma/apis/docs/xsd/rest_holding.xsd?tags&#x3D;POST) (required)
   * @return Object
   */
  @RequestMapping(method= RequestMethod.POST, value="/{mmsId}/holdings", consumes = MediaType.APPLICATION_XML_VALUE)
  HoldingWithRecord postBibsMmsIdHoldings(@RequestParam("mms_id") String mmsId, Object body);

  /**
   * Create Item
   * This web service creates an Item.
   * @param mmsId The Bib Record ID. (required)
   * @param holdingId The Holding Record ID. May be ALL to retrieve all holdings for a Bib. (required)
   * @param body This method takes an Item object. See [here](/alma/apis/docs/xsd/rest_item.xsd?tags&#x3D;POST) (required)
   * @return Object
   */
  @RequestMapping(method= RequestMethod.POST, value="/{mmsId}/holdings/{holdingId}/items")
  Item postBibsMmsIdHoldingsHoldingIdItems(@RequestParam("mms_id") String mmsId, @RequestParam("holding_id") String holdingId, Object body);

  /**
   * Scan-in operation on item.
   * This web service allows to perform a Scan-in operation on an item. The service imitates the behavior of the the Alma UI, therefore, parameters should be sent only if they appear in the specific scan-in page in the UI. For example, the Done parameter should only be sent when the scan-in is done at a department.
   * @param mmsId The Bib Record ID. (required)
   * @param holdingId The Holding Record ID. (required)
   * @param itemPid The Item ID. (required)
   * @param op The operation that is to be performed. Currently, only option is: scan. (required)
   * @param requestId The request ID. Needed when item is not yet bound to a single request and there are multiple requests which scanned item can fulfill. (required)
   * @param library The library code of the given circulation desk or department where the action is being performed. (required)
   * @param circDesk The circulation desk where the action is being performed. Send either this parameter or the department parameter. (required)
   * @param department The department where the action is being performed. Send either this parameter or the circ_desk parameter. (required)
   * @param workOrderType The work order type which is to be performed, or is being performed on the scanned in item. (required)
   * @param status The work order status to which we want to move the item. Optional input is defined by the work order type. (required)
   * @param externalId External ID. Options: true or false. (optional, default to &quot;false&quot;)
   * @param done Work order processing is completed on the item. Options: true or false. Only relevant when department parameter is sent. (optional, default to &quot;false&quot;)
   * @param autoPrintSlip Automatically print a slip. Options: true or false. (optional, default to &quot;false&quot;)
   * @param placeOnHoldShelf Place on hold shelf. Options: true or false. (optional, default to &quot;false&quot;)
   * @param confirm Confirm the action on the item. Options: true or false. (optional, default to &quot;false&quot;)
   * @param registerInHouseUse Register in house uses. Options: true or false. (optional, default to &quot;true&quot;)
   * @return Object
   */
  @RequestMapping(method= RequestMethod.POST, value="/{mmsId}/holdings/{holdingId}/items/{itemPid}?op={op}&external_id={externalId}&request_id={requestId}&library={library}&circ_desk={circDesk}&department={department}&work_order_type={workOrderType}&status={status}&done={done}&auto_print_slip={autoPrintSlip}&place_on_hold_shelf={placeOnHoldShelf}&confirm={confirm}&register_in_house_use={registerInHouseUse}")
  Item postBibsMmsIdHoldingsHoldingIdItemsItemPid(@RequestParam("mms_id") String mmsId, @RequestParam("holding_id") String holdingId, @RequestParam("item_pid") String itemPid, @RequestParam("op") String op, @RequestParam("request_id") String requestId, @RequestParam("library") String library, @RequestParam("circ_desk") String circDesk, @RequestParam("department") String department, @RequestParam("work_order_type") String workOrderType, @RequestParam("status") String status, @RequestParam("external_id") String externalId, @RequestParam("done") String done, @RequestParam("auto_print_slip") String autoPrintSlip, @RequestParam("place_on_hold_shelf") String placeOnHoldShelf, @RequestParam("confirm") String confirm, @RequestParam("register_in_house_use") String registerInHouseUse);


  /**
   * Create Portfolio for a Bib
   * This web service creates a Portfolio for a Bib.
   * @param mmsId The Bib Record ID. (required)
   * @param body This method takes a portfolio object. See [here](/alma/apis/docs/xsd/rest_portfolio.xsd?tags&#x3D;POST) (required)
   * @return Object
   */
  @RequestMapping(method= RequestMethod.POST, value="/{mmsId}/portfolios/")
  Portfolio postBibsMmsIdPortfolios(@RequestParam("mms_id") String mmsId, Object body);

  /**
   * Update Bib Record
   * This web service updates a Bib Record.   Note: JSON is not supported, and updating a linked CZ record is currently not supported.  For more information regarding the various options supported for this API see [here](https://developers.exlibrisgroup.com/blog/cataloging-APIs-enhancements).  For an IZ record that is linked to NZ record, local fields will be replaced - based on $$9local field indication.  Updating of non-local fields should be done directly on the NZ record.  See [Working with APIs in a Network Topology](https://developers.exlibrisgroup.com/blog/Working-with-APIs-in-a-Network-Topology) for more details.
   * @param mmsId The Bib Record ID. (required)
   * @param body This method takes a Bib object. See [here](/alma/apis/docs/xsd/rest_bib.xsd?tags&#x3D;PUT) (required)
   * @param normalization The id of the normalization profile to run. (optional, default to &quot;&quot;)
   * @param validate Indicating whether to check for errors. Default: false. (optional, default to &quot;false&quot;)
   * @param overrideWarning Indicating whether to ignore warnings. Default: true (record will be saved and the warnings will be added to the API output). (optional, default to &quot;true&quot;)
   * @param overrideLock Indicating whether to ignore lock. Default: true (record will be saved regardless if is currently being edited by another user). (optional, default to &quot;true&quot;)
   * @param staleVersionCheck Indicating whether to validate stale version of the record. When true, the 005 field of the MARC record must be identical to that of the record in the database. Default: false. (optional, default to &quot;false&quot;)
   * @param catalogerLevel Cataloger level of the user updating the record - used for validating that the level is sufficient. To change the record&#39;s level, use the payload&#39;s cataloging_level field. (optional, default to &quot;&quot;)
   * @param checkMatch Indicating whether to check for a match. Default: false (record will be saved despite possible match). (optional, default to &quot;false&quot;)
   * @return Object
   */
  @RequestMapping(method= RequestMethod.PUT, value="/{mmsId}?normalization={normalization}&validate={validate}&override_warning={overrideWarning}&override_lock={overrideLock}&stale_version_check={staleVersionCheck}&cataloger_level={catalogerLevel}&check_match={checkMatch}", consumes = MediaType.APPLICATION_XML_VALUE)
  Bib putBibsMmsId(@RequestParam("mms_id") String mmsId, Object body, @RequestParam("normalization") String normalization, @RequestParam("validate") String validate, @RequestParam("override_warning") String overrideWarning, @RequestParam("override_lock") String overrideLock, @RequestParam("stale_version_check") String staleVersionCheck, @RequestParam("cataloger_level") String catalogerLevel, @RequestParam("check_match") String checkMatch);


  /**
   * Update Holdings Record
   * This web service updates a Holdings Record.   Note: JSON is not supported for this API.
   * @param mmsId The Bib Record ID. (required)
   * @param holdingId The Holding Record ID. (required)
   * @param body This method takes a Holding object. See [here](/alma/apis/docs/xsd/rest_holding.xsd?tags&#x3D;PUT) (required)
   * @return Object
   */
  @RequestMapping(method= RequestMethod.PUT, value="/{mmsId}/holdings/{holdingId}", consumes = MediaType.APPLICATION_XML_VALUE)
  Holding putBibsMmsIdHoldingsHoldingId(@PathVariable String mmsId, @PathVariable String holdingId, Object body);

  /**
   * Update Item information
   * This web service updates Item information.
   * @param mmsId The Bib Record ID. (required)
   * @param holdingId The Holding Record ID. (required)
   * @param itemPid The Item ID. (required)
   * @param body This method takes an Item object. See [here](/alma/apis/docs/xsd/rest_item.xsd?tags&#x3D;PUT) (required)
   * @return Object
   */
  @RequestMapping(method= RequestMethod.PUT, value="/{mmsId}/holdings/{holdingId}/items/{itemPid}", consumes = MediaType.APPLICATION_XML_VALUE)
  Item putBibsMmsIdHoldingsHoldingIdItemsItemPid(@RequestParam("mms_id") String mmsId, @RequestParam("holding_id") String holdingId, @RequestParam("item_pid") String itemPid, Object body);

  /**
   * Update Portfolio for a Bib
   * This web service updates a Portfolio for a Bib.
   * @param mmsId The Bib Record ID. (required)
   * @param portfolioId Unique ID of the electronic portfolio. (required)
   * @param body This method takes a portfolio object. See [here](/alma/apis/docs/xsd/rest_portfolio.xsd?tags&#x3D;PUT) (required)
   * @return Object
   */
  @RequestMapping(method= RequestMethod.PUT, value="/{mmsId}/portfolios/{portfolioId}")
  Portfolio putBibsMmsIdPortfoliosPortfolioId(@RequestParam("mms_id") String mmsId, @RequestParam("portfolio_id") String portfolioId, Object body);
}

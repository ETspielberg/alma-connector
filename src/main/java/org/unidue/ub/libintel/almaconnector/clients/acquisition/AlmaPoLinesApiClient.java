package org.unidue.ub.libintel.almaconnector.clients.acquisition;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.unidue.ub.alma.shared.acq.*;

@FeignClient(name = "po-lines", url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1/acq/po-lines", configuration = AcquisitionFeignConfiguration.class)
@Service
public interface AlmaPoLinesApiClient {

    /**
     * Cancel PO-Line
     * This web service cancels a PO-Line.
     *
     * @param poLineId     The PO-Line number. (required)
     * @param reason       The cancellation reason code. From the &#39;PO Line Cancellation Reasons&#39; code table. (required)
     * @param comment      Comment regarding cancellation. (optional, default to &quot;&quot;)
     * @param informVendor Inform vendor of cancellation flag. Defaults to false. (optional, default to false)
     * @param override     Override errors flag. Defaults to false. (optional, default to false)
     * @param bib          Method for handling standalone Bib record: retain, delete or suppress. Optional. By default: retain. (optional, default to &quot;retain&quot;)
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "po-lines/{poLineId}?reason={reason}&comment={comment}&inform_vendor={informVendor}&override={override}&bib={bib}")
    void deletePoLinesPoLineId(@PathVariable("po_line_id") String poLineId, @RequestHeader("Accept") String accept, @RequestParam("reason") String reason, @RequestParam("comment") String comment, @RequestParam("inform_vendor") Boolean informVendor, @RequestParam("override") Boolean override, @RequestParam("bib") String bib);

    /**
     * Retrieve PO-Lines
     * This API returns a list of PO-Lines.
     *
     * @param q                      Search query. Optional. Searching for words from: title, author, mms_id, publisher, publication_year, publication_place, issn_isbn, shelving_location, vendor_code, vendor_name, vendor_account, fund_code, fund_name, number, po_number, invoice_reference &amp; all (for searching in all the above fields). It is also possible to search on fields from the related items (relevant for libraries who have one PO-Line per Item, and the maximum number of records which can be retrieved is limited): enumeration_a/b/c, chronology_i/j/k, item_description and item_library. Example (note the tilde between the code and text): q&#x3D;author~Mark (see [Brief Search](https://developers.exlibrisgroup.com/blog/How-we-re-building-APIs-at-Ex-Libris#BriefSearch)) (optional, default to &quot;&quot;)
     * @param status                 PO-Line Status. Optional. Valid values are CLOSED, CANCELLED, ACTIVE, ALL, ALL_WITH_CLOSED. Default: ALL (retrieves all PO lines except CLOSED). (optional, default to &quot;ALL&quot;)
     * @param limit                  Limits the number of results. Optional. Valid values are 0-100. Default value: 10. (optional)
     * @param offset                 Offset of the results returned. Optional. Default value: 0, which means that the first results will be returned. (optional)
     * @param orderBy                Order by parameter. Optional. The default is title. The Order by options are number, title, created_date and po_number. It is also possible to sort by fields from the related items (relevant for libraries who have one PO-Line per Item, and only works when the search query included item related fields): enumeration_a/b/c, chronology_i/j/k &amp; item_description. The secondary sort is on number. (optional, default to &quot;title&quot;)
     * @param direction              Direction of ordering. Optional. The choices are asc, desc. The default is desc. (optional, default to &quot;desc&quot;)
     * @param acquisitionMethod      Filter by acquisition method, e.g. PURCHASE. Optional. The default is to do no filtering. (optional, default to &quot;ALL&quot;)
     * @param expand                 The expand parameter allows for increasing the PO-Lines information with data on:  notes - The PO notes.  locations - The PO locations with their sub objects.  To get more than one, use a comma separator. Optional.  (optional, default to &quot;&quot;)
     * @param library                The code of the library that owns the PO lines. Optional. If supplied, only the PO Lines for this library will be retrieved. If not supplied, all the PO Lines that match the other parameters will be retrieved. (optional, default to &quot;&quot;)
     * @param minExpectedArrivalDate Retrieve PO lines with expected arrival date starting this Date (YYYY-MM-DD). Optional. (optional, default to &quot;&quot;)
     * @param maxExpectedArrivalDate Retrieve PO lines with expected arrival date until this Date (YYYY-MM-DD), included. Optional. (optional, default to &quot;&quot;)
     * @return Object
     */
    @RequestMapping(method=RequestMethod.GET)
    PoLines getPoLines(@RequestHeader("Accept") String accept, @RequestParam("q") String q, @RequestParam("status") String status, @RequestParam("limit") Integer limit, @RequestParam("offset") Integer offset, @RequestParam("order_by") String orderBy, @RequestParam("direction") String direction, @RequestParam("acquisition_method") String acquisitionMethod, @RequestParam("expand") String expand, @RequestParam("library") String library, @RequestParam("min_expected_arrival_date") String minExpectedArrivalDate, @RequestParam("max_expected_arrival_date") String maxExpectedArrivalDate);

    /**
     * Get PO-Line
     * This API returns a specific PO-Line.
     *
     * @param poLineId The PO-Line number. (required)
     * @return Object
     */
    @RequestMapping(method = RequestMethod.GET, value="/{poLineId}")
    PoLine getPoLinesPoLineId(@RequestHeader("Accept") String accept, @PathVariable("po_line_id") String poLineId);

    /**
     * Get PO-Line Items
     * This API returns the items related to a specific PO-Line.   The items retrieved include only barcodes and a link for the Get-Item API
     *
     * @param poLineId The PO-Line number. (required)
     * @return Object
     */
    @RequestMapping(method=RequestMethod.GET, value="/{poLineId}/items")
    Items getPoLinesPoLineIdItems(@RequestHeader("Accept") String accept, @PathVariable("po_line_id") String poLineId);

    /**
     * Create PO-Line
     * This web service creates a new PO-Line.     The PO line can be for physical, electronic or electronic collection. The PO line will be created along with the relevant inventory according to the &#39;type&#39; field. It will then be processed in Alma as any PO line.     The common use case for this web service is for creating a PO line in Alma, representing an order that was purchased at vendor system. See details [here](https://developers.exlibrisgroup.com/blog/Real-time-Acquisitions).   The following fields are required by the POST action: owner, type, title or mms_id. If they were not supplied - 400 HTTP error will be returned. There are other fields which are required for the PO line to be processed in Alma: vendor, vendor account, price and fund. If any of these fields is missing, the PO Line will be created in Alma, but will be in the &#39;In Review&#39; list.     Note that PO line processing in Alma is asynchronous. This means that the response for POST action might not include status and alerts. As part of the PO line details, the relevant metadata is sent to Alma. Alma will try to match it with existing bibliographic records. If a matching bibliographic record exists- the new PO line will be related to that record. If not, a new bibliographic record will be created including the relevant metadata.     For more information about configuring match rules see [here](https://developers.exlibrisgroup.com/blog/Create-PO-line-API-how-the-bibliographic-record-is-determined).
     *
     * @param body        This method creates a PO Line object. See [here](/alma/apis/docs/xsd/rest_po_line.xsd?tags&#x3D;POST) (required)
     * @param profileCode New Order API profile code. Optional. (optional, default to &quot;&quot;)
     * @return Object
     */
    @RequestMapping(method=RequestMethod.POST)
    PoLine postAcqPoLines(@RequestBody PoLine body, @RequestHeader("Accept") String accept, @RequestParam("profile_code") String profileCode);

    /**
     * Receive New Item
     * This web service adds an item to a PO-Line and receives it.
     *
     * @param poLineId          The PO-Line number. (required)
     * @param body              The item to add. See [here](/alma/apis/docs/xsd/rest_item.xsd?tags&#x3D;POST) (required)
     * @param receiveDate       The receive date. Default value is current time.  Expected Format: YYYY-MM-DDZ (optional, default to &quot;&quot;)
     * @param department        The code of the department where the item is being received. If not supplied, a random department will be chosen from the owning library&#39;s acquisition departments. (optional, default to &quot;&quot;)
     * @param departmentLibrary The library code of the department where the item is being received. (optional, default to &quot;&quot;)
     * @return Object
     */
    @RequestMapping(method=RequestMethod.POST, value="/{poLineId}/items")
    Item postPoLinesPoLineIdItems(@RequestBody Item body, @RequestHeader("Accept") String accept, @PathVariable("po_line_id") String poLineId, @RequestParam("receive_date") String receiveDate, @RequestParam("department") String department, @RequestParam("department_library") String departmentLibrary);

    /**
     * Receive an Existing Item
     * This web service updates an item to be considered received. If no payload is sent, the item information will not be changed besides the receive action. If an item object is sent in the payload, the item information will be updated accordingly.
     *
     * @param poLineId          The PO-Line number. (required)
     * @param itemId            The item ID. (required)
     * @param op                The operation to perform on the item. Currently, the only option is &#39;receive&#39; (required)
     * @param body              The item info for updating the item. If no update is required, an empty item object must be sent (e.g. &lt;item /&gt;). See [here](/alma/apis/docs/xsd/rest_item.xsd?tags&#x3D;POST) (required)
     * @param receiveDate       The receive date. Default value is current time.  Expected Format: YYYY-MM-DDZ (optional, default to &quot;&quot;)
     * @param department        The code of the department where the item is being received. If not supplied, a random department will be chosen from the owning library&#39;s acquisition departments. (optional, default to &quot;&quot;)
     * @param departmentLibrary The library code of the department where the item is being received. (optional, default to &quot;&quot;)
     * @return Object
     */
    @RequestMapping(method=RequestMethod.POST, value="/{poLineId}/items/{itemId}")
    PoLines postPoLinesPoLineIdItemsItemId(@RequestBody PoLines body, @RequestHeader("Accept") String accept, @PathVariable("poLineId") String poLineId, @PathVariable("itemId") String itemId, @RequestParam("op") String op, @RequestParam("receive_date") String receiveDate, @RequestParam("department") String department, @RequestParam("department_library") String departmentLibrary);

    /**
     * Update PO-Line
     * This web service updates a PO-Line. The PO line can be for physical, electronic or electronic collection.  Note: if the locations section includes a copy in a temporary location, the entire locations section will not be updated.
     *
     * @param poLineId        The PO-Line number. (required)
     * @param body            This method creates a PO Line object. See [here](/alma/apis/docs/xsd/rest_po_line.xsd?tags&#x3D;PUT) (required)
     * @param updateInventory Flag for updating the PO Line&#39;s inventory. Options: true, false. Default: true. (optional, default to &quot;true&quot;)
     * @return Object
     */
    @RequestMapping(method=RequestMethod.POST, value="/{poLineId}")
    PoLine putPoLinesPoLineId(@RequestBody PoLine body, @RequestHeader("Accept") String accept, @PathVariable("poLineId") String poLineId, @RequestParam("update_inventory") String updateInventory);

}

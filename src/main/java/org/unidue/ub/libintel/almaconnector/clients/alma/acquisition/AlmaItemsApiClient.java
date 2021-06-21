package org.unidue.ub.libintel.almaconnector.clients.alma.acquisition;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.unidue.ub.alma.shared.bibs.Item;
import org.unidue.ub.libintel.almaconnector.clients.alma.AlmaFeignConfiguration;

@FeignClient(name = "items", url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1", configuration = AlmaFeignConfiguration.class)
@Service
public interface AlmaItemsApiClient {

    /**
     * Get Item by Barcode
     * This API returns an item by its barcode.
     * @param itemBarcode barcode of the requested item.
     * @param view Invoice view. If view&#x3D;brief, invoices will be returned without lines. (optional, default to &quot;&quot;)
     * @return Invoices
     */
    @RequestMapping(method= RequestMethod.GET, value="/items")
    Item getItemByBarcode(@RequestHeader("Accept") String accept, @RequestParam("item_barcode") String itemBarcode, @RequestParam("view") String view);

    /**
     * updates an Item
     * @param accept the content type of the response (e.g. "application/json")
     * @param mmsId the mms id of the item
     * @param holdingId the holding id of the item
     * @param itemId the item id of the item
     * @param item the item object
     * @return the updated item
     */
    @RequestMapping(method= RequestMethod.PUT, value="/bibs/{mmsId}/holdings/{holdingId}/items/{itemId}")
    Item updateItem(@RequestHeader("Accept") String accept, @PathVariable String mmsId, @PathVariable String holdingId, @PathVariable String itemId, @RequestBody Item item);

    /**
     * Get Item by MMS ID, Holding ID and Item PID
     * @param accept the content type of the response body (e.g. "application/json")
     * @param mmsId the mms id of the item
     * @param holdingId the holding id of the item
     * @param itemId the item pid of the item
     * @return the item
     */
    @RequestMapping(method= RequestMethod.GET, value="/bibs/{mmsId}/holdings/{holdingId}/items/{itemId}")
    Item getItem(@RequestHeader("Accept") String accept, @PathVariable String mmsId, @PathVariable String holdingId, @PathVariable String itemId);
}

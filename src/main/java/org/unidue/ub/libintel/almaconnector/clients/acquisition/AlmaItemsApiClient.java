package org.unidue.ub.libintel.almaconnector.clients.acquisition;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.unidue.ub.alma.shared.bibs.Item;

@FeignClient(name = "items", url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1", configuration = AcquisitionFeignConfiguration.class)
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

    @RequestMapping(method= RequestMethod.PUT, value="/bibs/{mmsId}/holdings/{holdingId}/items/{itemId}")
    Item updateItem(@RequestHeader("Accept") String accept, @PathVariable String mmsId, @PathVariable String holdingId, @PathVariable String itemId, @RequestBody Item item);

    @RequestMapping(method= RequestMethod.GET, value="/bibs/{mmsId}/holdings/{holdingId}/items/{itemId}")
    Item getItem(@RequestHeader("Accept") String accept, @PathVariable String mmsId, @PathVariable String holdingId, @PathVariable String itemId);
}

package org.unidue.ub.libintel.almaconnector.clients.acquisition;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.unidue.ub.alma.shared.bibs.Item;

@FeignClient(name = "items", url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1/items", configuration = AcquisitionFeignConfiguration.class)
@Service
public interface AlmaItemsApiClient {

    /**
     * Get Item by Barcode
     * This API returns an item by its barcode.
     * @param itemBarcode barcode of the requested item.
     * @param view Invoice view. If view&#x3D;brief, invoices will be returned without lines. (optional, default to &quot;&quot;)
     * @return Invoices
     */
    @RequestMapping(method= RequestMethod.GET, value="/")
    Item getItem(@RequestHeader("Accept") String accept, @RequestParam("item_barcode") String itemBarcode, @RequestParam("view") String view);
}

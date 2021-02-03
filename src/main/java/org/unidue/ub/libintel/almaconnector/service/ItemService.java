package org.unidue.ub.libintel.almaconnector.service;

import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.Item;
import org.unidue.ub.libintel.almaconnector.clients.acquisition.AlmaItemsApiClient;
import org.unidue.ub.libintel.almaconnector.clients.bib.AlmaCatalogApiClient;
import org.unidue.ub.libintel.almaconnector.model.bubi.AlmaItemData;

import java.util.List;

@Service
public class ItemService {

    private final AlmaItemsApiClient almaItemsApiClient;

    private final AlmaCatalogApiClient almaCatalogApiClient;

    public ItemService(AlmaItemsApiClient almaItemsApiClient, AlmaCatalogApiClient almaCatalogApiClient) {
        this.almaItemsApiClient = almaItemsApiClient;
        this.almaCatalogApiClient = almaCatalogApiClient;
    }

    public Item findItemByBarcode(String barcode) {
        return this.almaItemsApiClient.getItemByBarcode("application/json", barcode, "");
    }

    public Item findItemByMmsAndItemId(String mmsId, String itemId) {
        return this.almaCatalogApiClient.getBibsMmsIdHoldingsHoldingIdItemsItemPid(mmsId, "ALL", itemId, "full", "", "");
    }
}

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
        return this.almaCatalogApiClient.getBibsMmsIdHoldingsHoldingIdItemsItemPid("application/json", mmsId, "ALL", itemId, "full", "", "");
    }

    public Item updateItem(Item item) {
        return this.almaItemsApiClient.updateItem("application/json", item.getBibData().getMmsId(), item.getHoldingData().getHoldingId(), item.getItemData().getPid(), item);
    }

    public Item updateItem(String mmsId, String holdingId, String itemPid, Item item) {
        return this.almaItemsApiClient.updateItem("application/json", mmsId, holdingId, itemPid, item);
    }
}

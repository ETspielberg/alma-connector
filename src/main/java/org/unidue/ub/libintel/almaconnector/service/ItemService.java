package org.unidue.ub.libintel.almaconnector.service;

import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.Item;
import org.unidue.ub.libintel.almaconnector.clients.acquisition.AlmaItemsApiClient;

@Service
public class ItemService {

    private final AlmaItemsApiClient almaItemsApiClient;

    public ItemService(AlmaItemsApiClient almaItemsApiClient) {
        this.almaItemsApiClient = almaItemsApiClient;
    }

    public Item findItemByBarcode(String barcode) {
        return this.almaItemsApiClient.getItemByBarcode("application/json", barcode, "");

    }
}

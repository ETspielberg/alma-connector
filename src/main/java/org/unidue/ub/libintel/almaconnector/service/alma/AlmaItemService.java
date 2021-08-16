package org.unidue.ub.libintel.almaconnector.service.alma;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.Item;
import org.unidue.ub.libintel.almaconnector.clients.alma.acquisition.AlmaItemsApiClient;
import org.unidue.ub.libintel.almaconnector.clients.alma.bib.AlmaCatalogApiClient;

/**
 * offers functions around items in Alma
 *
 * @author Eike Spielberg
 * @author eike.spielberg@uni-due.de
 * @version 1.0
 */
@Service
@Slf4j
public class AlmaItemService {

    private final AlmaItemsApiClient almaItemsApiClient;

    private final AlmaCatalogApiClient almaCatalogApiClient;

    /**
     * constructor based autowiring to the alma items api feign client and the alma bib api feign client
     * @param almaItemsApiClient the alma item api feign client
     * @param almaCatalogApiClient the alma bib api feign client
     */
    public AlmaItemService(AlmaItemsApiClient almaItemsApiClient, AlmaCatalogApiClient almaCatalogApiClient) {
        this.almaItemsApiClient = almaItemsApiClient;
        this.almaCatalogApiClient = almaCatalogApiClient;
    }

    /**
     * retrieves an item by its barcode
     * @param barcode the barcode of the item
     * @return the item
     */
    public Item findItemByBarcode(String barcode) {
        return this.almaItemsApiClient.getItemByBarcode("application/json", barcode, "");
    }

    public Item scanInItem(String barcode, boolean ready) {
        Item item;
        try {
            item = findItemByBarcode(barcode);
        } catch (FeignException fe) {
            log.warn("could not retrieve item by barcode " + barcode, fe);
            return null;
        }
        if (item.getItemData().getWorkOrderAt() != null) {
            log.info(item.toString());
            String library = "";
            String mmsId = item.getBibData().getMmsId();
            String holdingId = item.getHoldingData().getHoldingId();
            String itemId = item.getItemData().getPid();
            log.info(mmsId + " " + holdingId + " " + itemId);
            String workorderDepartment = item.getItemData().getWorkOrderAt().getValue();
            String workorderType = item.getItemData().getWorkOrderType().getValue();
            if (workorderDepartment.contains("AcqDept"))
                library = workorderDepartment.replace("AcqDept", "");
            try {
                item = this.almaCatalogApiClient.postBibsMmsIdHoldingsHoldingIdItemsItemPid(
                        item.getBibData().getMmsId(),
                        item.getHoldingData().getHoldingId(),
                        item.getItemData().getPid(),
                        "scan",
                        "",
                        library,
                        "",
                        workorderDepartment,
                        workorderType,
                        "",
                        "",
                        String.valueOf(ready),
                        "",
                        "",
                        "",
                        "");
            } catch (FeignException fe) {
                log.warn("could not scn in item " + item.toString(), fe);
            }
        }
        return item;
    }

    /**
     * retrieves an item by its mms and item id
     * @param mmsId the mms id of the bib record of the item
     * @param itemId the item id of the bib record of the item
     * @return the item
     */
    public Item findItemByMmsAndItemId(String mmsId, String itemId) {
        return this.almaCatalogApiClient.getBibsMmsIdHoldingsHoldingIdItemsItemPid("application/json", mmsId, "ALL", itemId, "full", "", "");
    }

    /**
     * updates an item in Alma
     * @param item the changed item
     * @return the updated item
     */
    public Item updateItem(Item item) {
        return this.almaItemsApiClient.updateItem("application/json", item.getBibData().getMmsId(), item.getHoldingData().getHoldingId(), item.getItemData().getPid(), item);
    }

    /**
     * updates an item in Alma
     * @param mmsId the mms id of the item
     * @param holdingId the holding id of the item
     * @param itemPid the item id of the item
     * @param item the changed item
     * @return the updated item
     */
    public Item updateItem(String mmsId, String holdingId, String itemPid, Item item) {
        return this.almaItemsApiClient.updateItem("application/json", mmsId, holdingId, itemPid, item);
    }
}

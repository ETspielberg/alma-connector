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
     *
     * @param almaItemsApiClient   the alma item api feign client
     * @param almaCatalogApiClient the alma bib api feign client
     */
    public AlmaItemService(AlmaItemsApiClient almaItemsApiClient, AlmaCatalogApiClient almaCatalogApiClient) {
        this.almaItemsApiClient = almaItemsApiClient;
        this.almaCatalogApiClient = almaCatalogApiClient;
    }

    /**
     * retrieves an item by its barcode
     *
     * @param barcode the barcode of the item
     * @return the item
     */
    public Item findItemByBarcode(String barcode) {
        return this.almaItemsApiClient.getItemByBarcode("application/json", barcode, "");
    }

    /**
     * scans in an item in its current position
     *
     * @param item thw item to be scanned
     * @return the item after the scan-in
     */
    public Item scanInItemDone(Item item) {
        if (item.getItemData().getWorkOrderAt() != null) {
            String library = "";
            String mmsId = item.getBibData().getMmsId();
            String holdingId = item.getHoldingData().getHoldingId();
            String itemId = item.getItemData().getPid();
            log.info(String.format("perfoming scan on item (mmsId | holdingId | itemId ): | %s | %s | %s", mmsId, holdingId, itemId));
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
                        "true",
                        "",
                        "",
                        "",
                        "");
            } catch (FeignException fe) {
                log.warn("could not scan in item " + item, fe);
            }
        }
        return item;
    }

    /**
     * scans an item at the default circulation desk of its home library
     * @param item the item to be scanned-in
     * @return the item after scan in
     */
    public Item scanInItemHomeLocation(Item item) {
        item = this.almaCatalogApiClient.postBibsMmsIdHoldingsHoldingIdItemsItemPid(
                item.getBibData().getMmsId(),
                item.getHoldingData().getHoldingId(),
                item.getItemData().getPid(),
                "scan",
                "",
                item.getItemData().getLibrary().getValue(),
                "DEFAULT_CIRC_DESK",
                "",
                "",
                "",
                "",
                "",
                "true",
                "",
                "",
                "");
        return item;
    }

    /**
     * retrieves an item by its mms and item id
     *
     * @param mmsId  the mms id of the bib record of the item
     * @param itemId the item id of the bib record of the item
     * @return the item
     */
    public Item findItemByMmsAndItemId(String mmsId, String itemId) {
        log.debug(String.format("retrieving item by mms id %s and item id %s", mmsId, itemId));
        return this.almaCatalogApiClient.getBibsMmsIdHoldingsHoldingIdItemsItemPid("application/json", mmsId, "ALL", itemId, "full", "", "");
    }

    /**
     * updates an item in Alma
     *
     * @param item the changed item
     * @return the updated item
     */
    public Item updateItem(Item item) {
        return this.almaCatalogApiClient.putBibsMmsIdHoldingsHoldingIdItemsItemPid(item.getBibData().getMmsId(), item.getHoldingData().getHoldingId(), item.getItemData().getPid(), item);
    }
}

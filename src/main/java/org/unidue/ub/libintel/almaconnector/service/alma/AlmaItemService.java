package org.unidue.ub.libintel.almaconnector.service.alma;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.Item;
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

    private final AlmaCatalogApiClient almaCatalogApiClient;


    /**
     * constructor based autowiring to the alma items api feign client and the alma bib api feign client
     *
     * @param almaCatalogApiClient the alma bib api feign client
     */
    public AlmaItemService(AlmaCatalogApiClient almaCatalogApiClient) {
        this.almaCatalogApiClient = almaCatalogApiClient;
    }

    /**
     * retrieves an item by its barcode
     *
     * @param barcode the barcode of the item
     * @return the item
     */
    public Item findItemByBarcode(String barcode) {
        return this.almaCatalogApiClient.getItemByBarcode(barcode, "");
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
        if (item.getItemData().getLibrary().getValue() == null) {
            String collection = item.getItemData().getLocation().getValue();
            if (collection == null || collection.isEmpty())
                return item;
            else
                item.getItemData().getLibrary().setValue(collection.charAt(0) + "0001");
        }
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
        try {
            return this.almaCatalogApiClient.getBibsMmsIdHoldingsHoldingIdItemsItemPid("application/json", mmsId, "ALL", itemId, "full", "", "");
        } catch (FeignException feignException) {
            log.warn("could not retrieve item from alma: " + feignException.getMessage());
            return null;
        }
    }

    /**
     * updates an item in Alma
     *
     * @param item the changed item
     * @return the updated item
     */
    public Item updateItem(Item item) {
        String mmsId = item.getBibData().getMmsId();
        String holdingId = item.getHoldingData().getHoldingId();
        String itemPid = item.getItemData().getPid();
        log.debug(String.format("updating item MMS-ID, Holding-ID, Item-ID | %s | %s | %s", mmsId, holdingId, itemPid));
        return this.almaCatalogApiClient.putBibsMmsIdHoldingsHoldingIdItemsItemPid(mmsId, holdingId, itemPid, item);
    }

    /**
     * updates an item in Alma
     *
     * @param item the changed item
     * @return the updated item
     */
    public Item updateItem(String mmsId, Item item) {
        String holdingId = item.getHoldingData().getHoldingId();
        String itemPid = item.getItemData().getPid();
        log.debug(String.format("updating item MMS-ID, Holding-ID, Item-ID | %s | %s | %s", mmsId, holdingId, itemPid));
        return this.almaCatalogApiClient.putBibsMmsIdHoldingsHoldingIdItemsItemPid(mmsId, holdingId, itemPid, item);
    }

    /**
     * performs a scan operation at a given location
     * @param library the library the scan shall be performed in
     * @param item the item to be scanned
     */
    public void scanInItemAtLocation(String library, Item item) {
        this.almaCatalogApiClient.postBibsMmsIdHoldingsHoldingIdItemsItemPid(
                item.getBibData().getMmsId(),
                item.getHoldingData().getHoldingId(),
                item.getItemData().getPid(),
                "scan",
                "",
                library,
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
    }

    /**
     * retreives an item from alma by its item id (setting mms-id to 'ALL')
     * @param itemId the ID of the item to be retrieved
     * @return the alma Item object
     */
    public Item findItemByItemId(String itemId) {
        try {
            return this.findItemByMmsAndItemId("ALL", itemId);
        } catch (FeignException fe) {
            log.warn(String.format("could not retrieve item %s from alma: %s", itemId, fe.getMessage()), fe);
            return null;
        }
    }

    /**
     * add the given note text to the public note, if it is not already present.
     * @param item the item holding the public note the note text should be added to
     * @param noteText the note text to be added
     */
    public void addPublicNote(Item item, String noteText) {
        String note = item.getItemData().getPublicNote();
        if (note == null || note.isEmpty()) {
            item.getItemData().setPublicNote(noteText);
            return;
        }
        if (note.contains(noteText))
            return;
        item.getItemData().setPublicNote(note + "; " + noteText);
    }

    /**
     * removes the given note text from the public note, if it is present.
     * @param item the item holding the public note the note text should be removed from
     * @param noteText the note text to be removed
     */
    public void removePublicNote(Item item, String noteText) {
        String note = item.getItemData().getPublicNote();
        if (note == null || note.isEmpty()) {
            return;
        }
        if (!note.contains(noteText))
            return;
        item.getItemData().setPublicNote(note.replace(noteText, "").trim());

    }
}

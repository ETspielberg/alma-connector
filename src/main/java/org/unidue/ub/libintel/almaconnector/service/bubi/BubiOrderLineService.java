package org.unidue.ub.libintel.almaconnector.service.bubi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.Item;
import org.unidue.ub.libintel.almaconnector.model.bubi.*;
import org.unidue.ub.libintel.almaconnector.repository.BubiOrderLineRepository;
import org.unidue.ub.libintel.almaconnector.service.PrimoService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaItemService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * offers functions around bubi order lines
 *
 * @author Eike Spielberg
 * @author eike.spielberg@uni-due.de
 * @version 1.0
 */
@Service
public class BubiOrderLineService {

    @Value("${libintel.bubi.journal.fund:55510-0-1100}")
    private String journalFund;

    @Value("${libintel.bubi.monograph.fund:55510-0-1200}")
    private String monographFund;

    private final BubiOrderLineRepository bubiOrderLineRepository;

    private final CoreDataService coreDataService;

    private final BubiDataService bubiDataService;

    private final AlmaItemService almaItemService;

    private final PrimoService primoService;

    private final Logger log = LoggerFactory.getLogger(BubiOrderService.class);

    /**
     * constructor based autowiring of the necessary services
     * @param bubiOrderLineRepository the bubi orderline repository
     * @param coreDataService the core data service
     * @param bubiDataService the bubi data service
     * @param almaItemService the alma item service
     * @param primoService the primo service
     */
    BubiOrderLineService(BubiOrderLineRepository bubiOrderLineRepository,
                         CoreDataService coreDataService,
                         BubiDataService bubiDataService,
                         AlmaItemService almaItemService,
                         PrimoService primoService) {
        this.bubiOrderLineRepository = bubiOrderLineRepository;
        this.coreDataService = coreDataService;
        this.bubiDataService = bubiDataService;
        this.almaItemService = almaItemService;
        this.primoService = primoService;
    }

    /**
     * saves a bubi orderline to the repositroy
     * @param bubiOrderLine the bubi orderline to be saved
     * @return the saved bubi orderline
     */
    public BubiOrderLine saveBubiOrderLine(BubiOrderLine bubiOrderLine) {
        bubiOrderLine.setLastChange(new Date());
        return this.bubiOrderLineRepository.save(bubiOrderLine);
    }

    /**
     * retreives all bubi orderlines by a given mode
     * @param mode the mode for which bubi orderlines shall be retrieved. implemented so far:
     *             'all': retreives all bubi orderlines
     *             'packed': retreives all bubi orderlines packed into a bubi order
     *             'sent': retreives all bubi orderlines collected by the bubi
     *             'waiting': retreives all bubi orderlines waiting to be collected
     *             other: retreives all active bubi orderlines
     * @return
     */
    public List<BubiOrderLine> getOrderLines(String mode) {
        switch (mode) {
            case "all":
                return this.bubiOrderLineRepository.findAll();
            case "packed":
                return this.bubiOrderLineRepository.findAllByStatusOrderByMinting(BubiStatus.PACKED);
            case "sent":
                return this.bubiOrderLineRepository.findAllByStatusOrderByMinting(BubiStatus.SENT);
            case "waiting":
                return this.bubiOrderLineRepository.findAllByStatusOrderByMinting(BubiStatus.WAITING);
            default:
                return getActiveOrderlines();
        }
    }

    /**
     * retrieves a bubi orderline by its identifier
     * @param identifier the identifier of the bubi orderline
     * @return the bubi orderline
     */
    public BubiOrderLine getBubiOrderLineFromIdentifier(String identifier) {
        return this.bubiOrderLineRepository.getBubiOrderLineByBubiOrderLineIdOrderByMinting(identifier);
    }

    /**
     * retrieves all bubi orderlines for a vendor
     * @param vendorId the id of the vendor
     * @return the list of bubi orderlines for this vendor
     */
    public List<BubiOrderLine> getAllBubiOrderLinesForBubi(String vendorId) {
        return this.bubiOrderLineRepository.findAllByVendorIdOrderByMinting(vendorId);
    }

    /**
     * retrieves a bubi orderline by the barcode of an item
     * @param barcode the barcode of the bubi orderline item
     * @return the bubi orderline
     */
    public BubiOrderLine getBubiOrderLineFromBarcode(String barcode) {
        if (barcode != null) {
            Item item = this.almaItemService.findItemByBarcode(barcode);
            return expandBubiOrderLineFromItem(item);
        }
        return null;
    }

    /**
     * retrieves a bubi orderline by the collection and shelfmark of an item
     * @param collection the collection of the item
     * @param shelfmark the shelfmark of the item
     * @return the bubi orderline for the item
     */
    public BubiOrderLine expandBubiOrderLineFromShelfmark(String collection, String shelfmark) {
        if (shelfmark != null && collection != null) {
            return retrieveBubiOrderLine(collection.toUpperCase(), shelfmark.toUpperCase());
        }
        return null;
    }

    /**
     * builds a bubi orderline for an item
     * @param item the item to create a bubi orderline for
     * @return the bubi orderline
     */
    public BubiOrderLine expandBubiOrderLineFromItem(Item item) {
        String collection = item.getItemData().getLocation().getDesc().toUpperCase(Locale.ROOT);
        String shelfmark = item.getHoldingData().getCallNumber().toUpperCase(Locale.ROOT);
        String campus = "E0001";
        try {
            campus = item.getItemData().getLibrary().getValue().toUpperCase(Locale.ROOT);
        } catch (Exception e) {
            if (collection.startsWith("D"))
                campus = "D0001";
        }
        String material = "book";
        if ("ISSBD".equals(item.getItemData().getPhysicalMaterialType().getValue()))
            material = "journal";
        long counter = this.bubiOrderLineRepository.countAllByShelfmarkAndCollection(shelfmark, collection);
        BubiOrderLine bubiOrderLine = new BubiOrderLine(collection, shelfmark, counter);
        log.info(String.format("retrieving core data for collection %s and shelfmark %s", bubiOrderLine.getCollection(), bubiOrderLine.getShelfmark()));
        CoreData coredata = this.coreDataService.getForCollectionAndShelfmark(collection, shelfmark);
        if (coredata == null) {
            log.info(String.format("no core data available - applying standard values for campus %s and material type %s", campus, material));
            coredata = this.coreDataService.findDefaultForMaterial(material, campus);
            bubiOrderLine.setTitle(item.getBibData().getTitle());
            bubiOrderLine.setAlmaMmsId(item.getBibData().getMmsId());
            bubiOrderLine.setAlmaHoldingId(item.getHoldingData().getHoldingId());
            bubiOrderLine.setAlmaItemId(item.getItemData().getPid());
            bubiOrderLine.addCoreData(coredata, true);
        } else {
            log.info("found core data");
            bubiOrderLine.addCoreData(coredata, false);
        }
        addDataFromVendor(bubiOrderLine);
        this.bubiOrderLineRepository.save(bubiOrderLine);
        return bubiOrderLine;
    }

    private boolean addCoreData(BubiOrderLine bubiOrderLine) {
        CoreData coredata = this.coreDataService.getForCollectionAndShelfmark(bubiOrderLine.getCollection(), bubiOrderLine.getShelfmark());
        if (coredata != null) {
            bubiOrderLine.addCoreData(coredata, false);
            return true;
        }
        return false;
    }

    private BubiOrderLine retrieveBubiOrderLine(String collection, String shelfmark) {
        long counter = this.bubiOrderLineRepository.countAllByShelfmarkAndCollection(shelfmark, collection);
        BubiOrderLine bubiOrderLine = new BubiOrderLine(collection, shelfmark, counter);
        log.info(String.format("retrieving core data for collection %s and shelfmark %s", bubiOrderLine.getCollection(), bubiOrderLine.getShelfmark()));
        boolean isDataAvailabe = addCoreData(bubiOrderLine);
        if (!isDataAvailabe) {
            AlmaItemData almaItemData = new AlmaItemData(collection, shelfmark);
            List<AlmaItemData> foundAlmaItemData = this.primoService.getPrimoResponse(almaItemData);
            if (foundAlmaItemData.size() > 0) {
                bubiOrderLine.addAlmaItemData(foundAlmaItemData.get(0));
            }
        }
        addDataFromVendor(bubiOrderLine);
        this.bubiOrderLineRepository.save(bubiOrderLine);
        return bubiOrderLine;
    }

    private void addDataFromVendor(BubiOrderLine bubiOrderline) {
        BubiData bubiData = bubiDataService.getVendorAccount(bubiOrderline.getVendorId(), bubiOrderline.getCollection());
        bubiOrderline.setVendorAccount(bubiData.getVendorAccount());
        if (bubiOrderline.getShelfmark().contains(" Z ")) {
            bubiOrderline.setFund(journalFund);
            bubiOrderline.setPrice(bubiData.getStandardPriceJournal());
        } else if (bubiOrderline.getStandard()) {
            bubiOrderline.setFund(monographFund);
            bubiOrderline.setPrice(bubiData.getStandardPriceMonograph());
        } else {
            bubiOrderline.setFund(monographFund);
            bubiOrderline.setPrice(bubiData.getStandardPriceMonograph());
        }
    }

    private List<BubiOrderLine> getActiveOrderlines() {
        List<BubiOrderLine> allOpenOrderlines = new ArrayList<>();
        allOpenOrderlines.addAll(this.bubiOrderLineRepository.findAllByStatusOrderByMinting(BubiStatus.NEW));
        allOpenOrderlines.addAll(this.bubiOrderLineRepository.findAllByStatusOrderByMinting(BubiStatus.WAITING));
        allOpenOrderlines.addAll(this.bubiOrderLineRepository.findAllByStatusOrderByMinting(BubiStatus.INWORK));
        return allOpenOrderlines;
    }
}

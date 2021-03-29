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

    public BubiOrderLine saveBubiOrderLine(BubiOrderLine bubiOrderLine) {
        bubiOrderLine.setLastChange(new Date());
        return this.bubiOrderLineRepository.save(bubiOrderLine);
    }

    public List<BubiOrderLine> getOrderLines(String mode) {
        switch (mode) {
            case "all":
                return this.bubiOrderLineRepository.findAll();
            case "packed":
                return this.bubiOrderLineRepository.findAllByStatus(BubiStatus.PACKED);
            case "sent":
                return this.bubiOrderLineRepository.findAllByStatus(BubiStatus.SENT);
            case "waiting":
                return this.bubiOrderLineRepository.findAllByStatus(BubiStatus.WAITING);
            default:
                return getActiveOrderlines();
        }
    }

    public List<BubiOrderLine> getActiveOrderlines() {
        List<BubiOrderLine> allOpenOrderlines = new ArrayList<>();
        allOpenOrderlines.addAll(this.bubiOrderLineRepository.findAllByStatus(BubiStatus.NEW));
        allOpenOrderlines.addAll(this.bubiOrderLineRepository.findAllByStatus(BubiStatus.WAITING));
        allOpenOrderlines.addAll(this.bubiOrderLineRepository.findAllByStatus(BubiStatus.INWORK));
        return allOpenOrderlines;
    }

    public BubiOrderLine getBubiOrderLineFromIdentifier(String identifier) {
        return this.bubiOrderLineRepository.getBubiOrderLineByBubiOrderLineId(identifier);
    }

    public List<BubiOrderLine> getAllBubiOrderLinesForBubi(String vendorId) {
        return this.bubiOrderLineRepository.findAllByVendorId(vendorId);
    }

    public BubiOrderLine getBubiOrderLineFromBarcode(String barcode) {
        if (barcode != null) {
            Item item = this.almaItemService.findItemByBarcode(barcode);
            return expandBubiOrderLineFromItem(item);
        }
        return null;
    }

    public BubiOrderLine expandBubiOrderLineFromShelfmark(String collection, String shelfmark) {
        if (shelfmark != null && collection != null) {
            return retrieveBubiOrderLine(collection.toUpperCase(), shelfmark.toUpperCase());
        }
        return null;
    }

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
        setFundAndPrice(bubiOrderLine);
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
        setFundAndPrice(bubiOrderLine);
        return bubiOrderLine;
    }

    private void setFundAndPrice(BubiOrderLine bubiOrderline) {
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
}

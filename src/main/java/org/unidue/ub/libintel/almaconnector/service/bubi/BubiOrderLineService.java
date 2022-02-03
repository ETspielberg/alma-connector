package org.unidue.ub.libintel.almaconnector.service.bubi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.Item;
import org.unidue.ub.libintel.almaconnector.model.bubi.*;
import org.unidue.ub.libintel.almaconnector.model.bubi.dto.AlmaItemData;
import org.unidue.ub.libintel.almaconnector.model.bubi.dto.BubiOrderLineBriefDto;
import org.unidue.ub.libintel.almaconnector.model.bubi.dto.BubiOrderLineFullDto;
import org.unidue.ub.libintel.almaconnector.model.bubi.dto.CoreDataFullDto;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.*;
import org.unidue.ub.libintel.almaconnector.repository.jpa.BubiOrderLinePositionRepository;
import org.unidue.ub.libintel.almaconnector.repository.jpa.BubiOrderLineRepository;
import org.unidue.ub.libintel.almaconnector.service.PriceNotFoundException;
import org.unidue.ub.libintel.almaconnector.service.PrimoService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaItemService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaSetService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * offers functions around bubi order lines
 *
 * @author Eike Spielberg
 * @author eike.spielberg@uni-due.de
 * @version 1.0
 */
@Service
@Slf4j
public class BubiOrderLineService {

    @Value("${libintel.funds.bubi.journal:55510-0-1100}")
    private String journalFund;

    @Value("${libintel.funds.bubi.monograph:55510-0-1200}")
    private String monographFund;

    private final BubiOrderLineRepository bubiOrderLineRepository;

    private final BubiOrderService bubiOrderService;

    private final CoreDataService coreDataService;

    private final BubiDataService bubiDataService;

    private final AlmaItemService almaItemService;

    private final AlmaSetService almaSetService;

    private final BubiPricesService bubiPricesService;

    private final BubiOrderLinePositionRepository bubiOrderLinePositionRepository;

    private final PrimoService primoService;

    /**
     * constructor based autowiring of the necessary services
     *
     * @param bubiOrderLineRepository the bubi orderline repository
     * @param coreDataService         the core data service
     * @param bubiDataService         the bubi data service
     * @param almaItemService         the alma item service
     * @param primoService            the primo service
     */
    BubiOrderLineService(BubiOrderService bubiOrderService,
                         BubiOrderLineRepository bubiOrderLineRepository,
                         BubiOrderLinePositionRepository bubiOrderLinePositionRepository,
                         CoreDataService coreDataService,
                         BubiDataService bubiDataService,
                         BubiPricesService bubiPricesService,
                         AlmaItemService almaItemService,
                         AlmaSetService almaSetService,
                         PrimoService primoService) {
        this.bubiOrderService = bubiOrderService;
        this.bubiOrderLineRepository = bubiOrderLineRepository;
        this.bubiOrderLinePositionRepository = bubiOrderLinePositionRepository;
        this.coreDataService = coreDataService;
        this.bubiDataService = bubiDataService;
        this.bubiPricesService = bubiPricesService;
        this.almaItemService = almaItemService;
        this.almaSetService = almaSetService;
        this.primoService = primoService;
    }

    /**
     * saves a bubi orderline full DTO to the repositroy, adds positions to set
     *
     * @param bubiOrderLineFullDto the bubi orderline full DTO to be saved
     * @return the saved bubi orderline
     */
    public BubiOrderLine saveBubiOrderLineFullDTO(BubiOrderLineFullDto bubiOrderLineFullDto) {
        // load the existing orderline from the database
        BubiOrderLine bubiOrderLine = bubiOrderLineRepository.getBubiOrderLineByBubiOrderLineIdOrderByMinting(bubiOrderLineFullDto.getBubiOrderLineId());

        //update the changed fields retrieved by the DTO
        bubiOrderLineFullDto.updateBubiOrderLine(bubiOrderLine);

        // set the connection between the positions and the orderline and save the positions
        for (BubiOrderlinePosition bubiOrderlinePosition : bubiOrderLine.getBubiOrderlinePositions()) {
            bubiOrderlinePosition.setBubiOrderLine(bubiOrderLine);
            this.bubiOrderLinePositionRepository.save(bubiOrderlinePosition);
        }

        // try to calculate the price for the orderline
        try {
            this.bubiPricesService.calculatePriceForOrderline(bubiOrderLine);
        } catch (PriceNotFoundException pnfe) {
            log.warn("could not calculate price - no price information available", pnfe);
        }

        // retrieve the order id
        String bubiOrderId = bubiOrderLineFullDto.getBubiOrderId();

        // if an order is given (id not null and not empty) retrieve the order and do the connections and set processing
        if (bubiOrderId != null && !bubiOrderId.isEmpty()) {

            // retrieve the order
            BubiOrder bubiOrder = this.bubiOrderService.getBubiOrder(bubiOrderId);

            // if there is no corresponding order, create a new one using the non-existing orderId as new order (and set) name
            if (bubiOrder == null)
                bubiOrder = this.bubiOrderService.createNewBubiOrder(bubiOrderLineFullDto.getBubiOrderId(), bubiOrderLine);

            log.info(String.format("retrieving bubi order | orderId: %s, almaSetName: %s, almaSetId: %s", bubiOrderId, bubiOrder.getAlmaSetName(), bubiOrder.getAlmaSetId()));

            // set the positional number (if not already set), the bubi order, and the status for the orderline
            if (bubiOrderLine.getPositionalNumber() <= 0)
                bubiOrderLine.setPositionalNumber(bubiOrder.getBubiOrderLines().size() + 1);
            bubiOrderLine.setBubiOrder(bubiOrder);
            bubiOrderLine.setStatus(BubiStatus.PACKED);

            // handle the set operations, if the bubiOrder has a set id
            if (bubiOrder.getAlmaSetId() != null && !bubiOrder.getAlmaSetId().isEmpty()) {
                // the stored orderline has still the set id of the old order (the set id is not updated by the DTO)
                String oldSetId = bubiOrderLine.getAlmaSetId();

                // compare the old set id from the retreived orderline with the set id from the order. if it is not the same (not null and not empty), remove the items from the old set. Then add the items to the new set.
                if (oldSetId != null && !oldSetId.isEmpty() && !oldSetId.equals(bubiOrder.getAlmaSetId()))
                    this.almaSetService.removePositionsFromSet(oldSetId, bubiOrderLine);

                // add the positions to the new set
                this.almaSetService.addPositionsToSet(bubiOrder.getAlmaSetId(), bubiOrderLine);

                // set the new set id
                bubiOrderLine.setAlmaSetId(bubiOrder.getAlmaSetId());
            }
        } else
            bubiOrderLine.setStatus(BubiStatus.WAITING);
        this.bubiOrderLineRepository.save(bubiOrderLine);
        return bubiOrderLine;
    }

    /**
     * retreives all bubi orderlines by a given mode
     *
     * @param mode the mode for which bubi orderlines shall be retrieved. implemented so far:
     *             'all': retrieves all bubi orderlines
     *             'packed': retrieves all bubi orderlines packed into a bubi order
     *             'sent': retrieves all bubi orderlines collected by the bubi
     *             'waiting': retrieves all bubi orderlines waiting to be collected
     *             other: retrieves all active bubi orderlines
     * @return a list of bubi orderlines
     */
    public List<BubiOrderLineBriefDto> getOrderLines(String mode) {
        List<BubiOrderLineBriefDto> orderlines = new ArrayList<>();
        switch (mode) {
            case "all":
                this.bubiOrderLineRepository.findAll().forEach(entry -> orderlines.add(new BubiOrderLineBriefDto(entry)));
                break;
            case "packed":
                this.bubiOrderLineRepository.findAllByStatusOrderByMinting(BubiStatus.PACKED).forEach(entry -> orderlines.add(new BubiOrderLineBriefDto(entry)));
                break;
            case "sent":
                this.bubiOrderLineRepository.findAllByStatusOrderByMinting(BubiStatus.SENT).forEach(entry -> orderlines.add(new BubiOrderLineBriefDto(entry)));
                break;
            case "waiting":
                this.bubiOrderLineRepository.findAllByStatusOrderByMinting(BubiStatus.WAITING).forEach(entry -> orderlines.add(new BubiOrderLineBriefDto(entry)));
                break;
            default:
                getActiveOrderlines().forEach(entry -> orderlines.add(new BubiOrderLineBriefDto(entry)));
                break;
        }
        return orderlines;
    }

    /**
     * retrieves a bubi orderline by its identifier
     *
     * @param identifier the identifier of the bubi orderline
     * @return the bubi orderline
     */
    public BubiOrderLineFullDto getBubiOrderLineFromIdentifier(String identifier) {
        BubiOrderLine bubiOrderLine = this.bubiOrderLineRepository.getBubiOrderLineByBubiOrderLineIdOrderByMinting(identifier);
        if (bubiOrderLine.getBubiOrderlinePositions() == null || bubiOrderLine.getBubiOrderlinePositions().size() == 0) {
            Set<BubiOrderlinePosition> positions = new HashSet<>();
            positions.add(new BubiOrderlinePosition());
            bubiOrderLine.setBubiOrderlinePositions(positions);
        }
        return new BubiOrderLineFullDto(bubiOrderLine);
    }

    /**
     * retrieves all bubi orderlines for a vendor
     *
     * @param vendorId the id of the vendor
     * @return the list of bubi orderlines for this vendor
     */
    public List<BubiOrderLineBriefDto> getAllBubiOrderLinesForBubi(String vendorId) {
        List<BubiOrderLineBriefDto> orderlines = new ArrayList<>();
        this.bubiOrderLineRepository.findAllByVendorAccountOrderByMinting(vendorId).forEach(entry -> orderlines.add(new BubiOrderLineBriefDto(entry)));
        return orderlines;
    }

    /**
     * retrieves a bubi orderline by the barcode of an item
     *
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
     *
     * @param collection the collection of the item
     * @param shelfmark  the shelfmark of the item
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
     *
     * @param item the item to create a bubi orderline for
     * @return the bubi orderline
     */
    public BubiOrderLine expandBubiOrderLineFromItem(Item item) {
        // determine collection, shelfmark and campus from item data
        String collection = item.getItemData().getLocation().getDesc().toUpperCase(Locale.ROOT);
        String shelfmark = item.getHoldingData().getCallNumber().toUpperCase(Locale.ROOT);
        String campus = "E0001";
        try {
            campus = item.getItemData().getLibrary().getValue().toUpperCase(Locale.ROOT);
        } catch (Exception e) {
            if (collection.startsWith("D"))
                campus = "D0001";
        }
        // determine material type
        String material;
        if ("ISSBD".equals(item.getItemData().getPhysicalMaterialType().getValue()))
            material = MediaType.JOURNAL.name();
        else
            material = MediaType.BOOK.name();
        // determine counter and create new orderline
        long counter = this.bubiOrderLineRepository.countAllByShelfmarkAndCollection(shelfmark, collection);
        BubiOrderLine bubiOrderLine = new BubiOrderLine(collection, shelfmark, counter);
        // search for appropriate core data
        log.info(String.format("retrieving core data for collection %s and shelfmark %s", bubiOrderLine.getCollection(), bubiOrderLine.getShelfmark()));
        CoreData coredata = this.coreDataService.getForCollectionAndShelfmark(collection, shelfmark);
        // if no core data are found (usually for monographs), build standard collective orderline
        if (coredata == null) {
            // first retrieve the standard coredata
            log.info(String.format("no core data available - applying standard values for campus %s and material type %s", campus, material));
            coredata = this.coreDataService.findDefaultForMaterial(material, campus);
            bubiOrderLine.setTitle(item.getBibData().getTitle());
            bubiOrderLine.setAlmaMmsId(item.getBibData().getMmsId());
            BubiOrderlinePosition position = new BubiOrderlinePosition()
                    .withDescription(item.getBibData().getTitle())
                    .withInternalNote(collection + ": " + item.getItemData().getAlternativeCallNumber())
                    .withMmsId(item.getBibData().getMmsId())
                    .withHoldingId(item.getHoldingData().getHoldingId())
                    .withItemId(item.getItemData().getPid());
            position.setBubiOrderLine(bubiOrderLine);
            bubiOrderLine.addPosition(position);
            bubiOrderLine.addCoreData(coredata);
            this.bubiOrderLineRepository.save(bubiOrderLine);
            this.bubiOrderLinePositionRepository.save(position);

        } else {
            // if coredata are found, the properties for execution (cover, binding etc.) are added from the core data.
            log.debug("found core data");
            bubiOrderLine.addCoreData(coredata);
            // in addition, the mms and holding ids are set in the position
            bubiOrderLine.addPositionFromCoredata(coredata);
        }
        // data concerning the vendor are added
        addDataFromVendor(bubiOrderLine);
        // the price ist calculated
        this.bubiPricesService.calculatePriceForOrderline(bubiOrderLine);
        // the orderline is saved
        this.bubiOrderLineRepository.save(bubiOrderLine);
        return bubiOrderLine;
    }

    /**
     * removes an orderline position from its parent orderline. If it is not a standard orderline, the position is just deleted
     * Otherwise, the orderline is cloned in the status NEW and the position is attached
     *
     * @param bubiOrderLineId         the id of the orderline from which the position is to be removed
     * @param bubiOrderlinePositionId the id of the position which shall be removed
     * @return the updated bubi orderline data transfer object without the removed position
     */
    public BubiOrderLineFullDto removeOrderlinePosition(String bubiOrderLineId, long bubiOrderlinePositionId) {
        // retrieve orderline from repository
        BubiOrderLine bubiOrderLine = this.bubiOrderLineRepository.findById(bubiOrderLineId).orElse(null);
        if (bubiOrderLine == null)
            return null;
        // get the desired position to be removed
        BubiOrderlinePosition positionToBeRemoved = bubiOrderLine.getBubiOrderlinePositions()
                .stream()
                .filter(entry -> entry.getBubiOrderPositionId() == bubiOrderlinePositionId)
                .collect(Collectors.toSet())
                .stream()
                .findFirst()
                .orElse(null);
        // if no position with the provided id is found, return teh original orderline
        if (positionToBeRemoved == null)
            return new BubiOrderLineFullDto(bubiOrderLine);
        // remove the position from the set of positions in the orderline
        bubiOrderLine.setBubiOrderlinePositions(bubiOrderLine.getBubiOrderlinePositions()
                .stream()
                .filter(entry -> entry.getBubiOrderPositionId() != bubiOrderlinePositionId)
                .collect(Collectors.toSet()));
        bubiOrderLine.setLastChange(new Date());
        bubiOrderLine = this.bubiOrderLineRepository.save(bubiOrderLine);

        // if the position has an alma item id and if the orderline is part of a bubi order (that is a corresponding
        // set exists), the item is removed from that set.
        if (positionToBeRemoved.getAlmaItemId() != null && !positionToBeRemoved.getAlmaItemId().isEmpty() && bubiOrderLine.getBubiOrder() != null) {
            this.almaSetService.removeMemberFromSet(positionToBeRemoved.getAlmaItemId(), bubiOrderLine.getBubiOrder().getAlmaSetId());
        }
        // if the position to be removed comes from a standard orderline, a new standard orderline is created and the position attached.
        if (bubiOrderLine.getStandard()) {
            BubiOrderLine newBubiOrderLine = bubiOrderLine.clone();
            // calculate the new counter
            long counter = this.bubiOrderLineRepository.countAllByShelfmarkAndCollection(bubiOrderLine.getShelfmark(), bubiOrderLine.getCollection());
            newBubiOrderLine.setCounter(counter + 1);
            // detach the orderline from the order and reset the status to NEW
            newBubiOrderLine.setBubiOrder(null);
            newBubiOrderLine.setStatus(BubiStatus.NEW);
            // set the position of the orderline
            Set<BubiOrderlinePosition> positions = new HashSet<>();
            positions.add(positionToBeRemoved);
            newBubiOrderLine.setBubiOrderlinePositions(positions);
            // recalculate the price
            this.bubiPricesService.calculatePriceForOrderline(newBubiOrderLine);
            // save the new orderline, attach it to the position and save the position
            this.bubiOrderLineRepository.save(newBubiOrderLine);
            positionToBeRemoved.setBubiOrderLine(newBubiOrderLine);
            this.bubiOrderLinePositionRepository.save(positionToBeRemoved);
        }
        // if it is not a standard orderline, the position is just deleted
        else {
            this.bubiOrderLinePositionRepository.delete(positionToBeRemoved);
        }
        return new BubiOrderLineFullDto(bubiOrderLine);
    }

    private boolean addCoreData(BubiOrderLine bubiOrderLine) {
        CoreData coredata = this.coreDataService.getForCollectionAndShelfmark(bubiOrderLine.getCollection(), bubiOrderLine.getShelfmark());
        if (coredata != null) {
            bubiOrderLine.addCoreData(coredata);
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
        BubiData bubiData = bubiDataService.getVendorAccount(bubiOrderline.getVendorAccount(), bubiOrderline.getCollection());
        bubiOrderline.setVendorAccount(bubiData.getVendorAccount());
        if (bubiOrderline.getShelfmark().contains(" Z ")) {
            bubiOrderline.setFund(journalFund);
        } else if (bubiOrderline.getStandard()) {
            bubiOrderline.setFund(monographFund);
        } else {
            bubiOrderline.setFund(monographFund);
        }
        this.bubiPricesService.calculatePriceForOrderline(bubiOrderline);
    }

    private List<BubiOrderLine> getActiveOrderlines() {
        List<BubiOrderLine> allOpenOrderlines = new ArrayList<>();
        allOpenOrderlines.addAll(this.bubiOrderLineRepository.findAllByStatusOrderByMinting(BubiStatus.NEW));
        allOpenOrderlines.addAll(this.bubiOrderLineRepository.findAllByStatusOrderByMinting(BubiStatus.WAITING));
        allOpenOrderlines.addAll(this.bubiOrderLineRepository.findAllByStatusOrderByMinting(BubiStatus.INWORK));
        allOpenOrderlines.addAll(this.bubiOrderLineRepository.findAllByStatusOrderByMinting(BubiStatus.PACKED));
        return allOpenOrderlines;
    }

    public BubiOrderLine getBubiOrderLineFromCoredata(String coredataId) {
        CoreDataFullDto coreData = this.coreDataService.getCoreDatum(coredataId);
        return this.retrieveBubiOrderLine(coreData.getCollection(), coreData.getShelfmark());
    }
}

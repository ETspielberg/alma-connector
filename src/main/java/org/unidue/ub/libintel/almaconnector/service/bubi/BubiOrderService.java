package org.unidue.ub.libintel.almaconnector.service.bubi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.acq.*;
import org.unidue.ub.alma.shared.bibs.HoldingDataTempLibrary;
import org.unidue.ub.alma.shared.bibs.HoldingDataTempLocation;
import org.unidue.ub.alma.shared.bibs.Item;
import org.unidue.ub.alma.shared.conf.*;
import org.unidue.ub.alma.shared.conf.Set;
import org.unidue.ub.libintel.almaconnector.model.bubi.*;
import org.unidue.ub.libintel.almaconnector.model.bubi.dto.BubiOrderBriefDto;
import org.unidue.ub.libintel.almaconnector.model.bubi.dto.BubiOrderFullDto;
import org.unidue.ub.libintel.almaconnector.model.bubi.dto.BubiOrderShortDto;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrder;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrderLine;
import org.unidue.ub.libintel.almaconnector.repository.BubiOrderLineRepository;
import org.unidue.ub.libintel.almaconnector.repository.BubiOrderRepository;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaInvoiceService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaItemService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaSetService;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * offers functions around bubi orders
 *
 * @author Eike Spielberg
 * @author eike.spielberg@uni-due.de
 * @version 1.0
 */
@Service
@Slf4j
public class BubiOrderService {

    private final BubiOrderRepository bubiOrderRepository;

    private final BubiOrderLineRepository bubiOrderLineRepository;

    private final AlmaInvoiceService almaInvoiceService;

    private final AlmaItemService almaItemService;

    private final AlmaSetService almaSetService;

    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

    /**
     * constructor based autowiring of the desired services
     *
     * @param bubiOrderRepository     the bubi order repository
     * @param bubiOrderLineRepository the bubi orderline repository
     * @param almaInvoiceService      the alma invoices service
     * @param almaItemService         the alma item service
     */
    public BubiOrderService(
            BubiOrderRepository bubiOrderRepository,
            BubiOrderLineRepository bubiOrderLineRepository,
            AlmaInvoiceService almaInvoiceService,
            AlmaItemService almaItemService,
            AlmaSetService almaSetService) {
        this.bubiOrderLineRepository = bubiOrderLineRepository;
        this.bubiOrderRepository = bubiOrderRepository;
        this.almaInvoiceService = almaInvoiceService;
        this.almaItemService = almaItemService;
        this.almaSetService = almaSetService;
    }

    /**
     * retrieves a bubi order by its id
     *
     * @param bubiOrderId the id of the bubi order
     * @return the bubi order
     */
    public BubiOrder getBubiOrder(String bubiOrderId) {
        Optional<BubiOrder> option = this.bubiOrderRepository.findById(bubiOrderId);
        return option.orElse(null);
    }

    /**
     * retrieves a bubi order by its id
     *
     * @param bubiOrderId the id of the bubi order
     * @return the bubi order
     */
    public BubiOrderFullDto getBubiOrderFull(String bubiOrderId) {
        Optional<BubiOrder> option = this.bubiOrderRepository.findById(bubiOrderId);
        return option.map(BubiOrderFullDto::new).orElse(null);
    }

    /**
     * retrieves a list of bubi orders by a given mode
     *
     * @param mode the mode for which the bubi orders are to be retrieved. currently supported:
     *             'all': retrieves all bubi orders from the repository
     *             'sent': retrieves all sent, but not yet returned bubi orders
     *             'complaint': retrieves all bubi orders with complaints
     *             'closed': retrieves all bubi orders which are already closed
     *             in all other cases all active bubi orders are retrieved (all except closed
     * @return a list of bubi order
     */
    public List<BubiOrderBriefDto> getBriefBubiOrders(String mode) {
        List<BubiOrder> orderEntities = getBubiOrderEntites(mode);
        List<BubiOrderBriefDto> orders = new ArrayList<>();
        orderEntities.forEach(entry -> orders.add(new BubiOrderBriefDto(entry)));
        return orders;
    }

    /**
     * retrieves a list of bubi orders by a given mode
     *
     * @param mode the mode for which the bubi orders are to be retrieved. currently supported:
     *             'all': retrieves all bubi orders from the repository
     *             'sent': retrieves all sent, but not yet returned bubi orders
     *             'complaint': retrieves all bubi orders with complaints
     *             'closed': retrieves all bubi orders which are already closed
     *             in all other cases all active bubi orders are retrieved (all except closed
     * @return a list of bubi order
     */
    public List<BubiOrderShortDto> getShortBubiOrders(String mode) {
        List<BubiOrder> orderEntities = getBubiOrderEntites(mode);
        List<BubiOrderShortDto> orders = new ArrayList<>();
        orderEntities.forEach(entry -> orders.add(new BubiOrderShortDto(entry)));
        return orders;
    }

    private List<BubiOrder> getBubiOrderEntites(String mode) {
        List<BubiOrder> orderEntities = new ArrayList<>();
        switch (mode) {
            case "all":
                this.bubiOrderRepository.findAll().forEach(orderEntities::add);
                break;
            case "sent":
                orderEntities = this.bubiOrderRepository.findAllByBubiStatusOrderByBubiOrderId(BubiStatus.SENT);
                break;
            case "complaint":
                orderEntities = this.bubiOrderRepository.findAllByBubiStatusOrderByBubiOrderId(BubiStatus.COMPLAINT);
                break;
            case "closed":
                orderEntities = this.bubiOrderRepository.findAllByBubiStatusOrderByBubiOrderId(BubiStatus.CLOSED);
                break;
            default: {
                orderEntities.addAll(this.bubiOrderRepository.findAllByBubiStatusOrderByBubiOrderId(BubiStatus.AT_BUBI));
                orderEntities.addAll(this.bubiOrderRepository.findAllByBubiStatusOrderByBubiOrderId(BubiStatus.PACKED));
                orderEntities.addAll(this.bubiOrderRepository.findAllByBubiStatusOrderByBubiOrderId(BubiStatus.NEW));
                orderEntities.addAll(this.bubiOrderRepository.findAllByBubiStatusOrderByBubiOrderId(BubiStatus.SENT));
                orderEntities.addAll(this.bubiOrderRepository.findAllByBubiStatusOrderByBubiOrderId(BubiStatus.WAITING));
                orderEntities.addAll(this.bubiOrderRepository.findAllByBubiStatusOrderByBubiOrderId(BubiStatus.COMPLAINT));
                orderEntities.addAll(this.bubiOrderRepository.findAllByBubiStatusOrderByBubiOrderId(BubiStatus.RETURNED));
                orderEntities.addAll(this.bubiOrderRepository.findAllByBubiStatusOrderByBubiOrderId(BubiStatus.READY));
                orderEntities.sort(Comparator.comparing(BubiOrder::getBubiOrderId));
            }
        }
        return orderEntities;
    }

    /**
     * packs a bubi order to be retrieved by the bubi
     *
     * @param bubiOrder the initial bubi order to be packed
     * @return a list of bubiorders grouping the initial bubi order by the vendor accounts
     */
    public List<BubiOrderFullDto> packBubiOrder(BubiOrder bubiOrder) {
        Hashtable<String, BubiOrder> bubiOrders = new Hashtable<>();
        for (BubiOrderLine bubiOrderLine : bubiOrder.getBubiOrderLines()) {
            String key = bubiOrderLine.getVendorAccount();
            if (bubiOrders.containsKey(key))
                bubiOrders.get(key).addBubiOrderLine(bubiOrderLine);
            else {
                long counter = this.bubiOrderRepository.countAllByVendorAccount(bubiOrderLine.getVendorAccount()) + 1;
                BubiOrder bubiOrderInd = new BubiOrder(bubiOrderLine.getVendorAccount(), counter);
                bubiOrderInd.addBubiOrderLine(bubiOrderLine);
                this.bubiOrderRepository.save(bubiOrderInd);
                bubiOrderLine.setBubiOrder(bubiOrderInd);
                this.bubiOrderLineRepository.save(bubiOrderLine);
                bubiOrders.put(key, bubiOrderInd);
            }
        }
        bubiOrders.forEach(
                (key, order) -> {
                    Members members = new Members();
                    this.bubiOrderRepository.save(order);
                    order.setBubiStatus(BubiStatus.NEW);
                    order.setLastChange(new Date());
                    order.sortBubiOrderLines();
                    order.calculateTotalPrice();
                    String date = new SimpleDateFormat(DATE_FORMAT_NOW).format(new Date());
                    String description = order.getVendorAccount() + " " + date;
                    org.unidue.ub.alma.shared.conf.Set set = this.almaSetService.createSet(order.getAlmaSetName(), description);
                    order.getBubiOrderLines().forEach(
                            orderline -> {
                                Member member = new Member().id(orderline.getAlmaItemId()).description(orderline.getCollection() + " " + orderline.getShelfmark());
                                members.addMemberItem(member);
                                orderline.setStatus(BubiStatus.PACKED);
                                orderline.setLastChange(new Date());
                                this.bubiOrderLineRepository.save(orderline);
                                this.almaSetService.addMemberToSet(set.getId(), orderline.getAlmaItemId(), orderline.getTitle());
                            });
                    this.bubiOrderRepository.save(order);
                }
        );
        List<BubiOrderFullDto> orders = new ArrayList<>();
        bubiOrders.values().forEach(entry -> orders.add(new BubiOrderFullDto(entry)));
        return orders;
    }

    /**
     * marks a bubi order and the corresponding bubi order lines as collected, sets the corresponding dates and creates the alma po lines
     *
     * @param bubiOrderId the bubi order to be collected
     * @return the updated bubi order object
     */
    public BubiOrderFullDto collectBubiOrder(String bubiOrderId) {
        BubiOrder bubiOrder = this.bubiOrderRepository.findById(bubiOrderId).orElse(null);
        if (bubiOrder == null) return null;
        bubiOrder.setBubiStatus(BubiStatus.AT_BUBI);
        bubiOrder.setLastChange(new Date());
        return new BubiOrderFullDto(bubiOrderRepository.save(bubiOrder));
    }

    /**
     * updates an existing bubi order with the data provided by the input
     * @param bubiOrderUpdate the data transfer object holding the changed data
     * @return the updated bubi order
     */
    public BubiOrderFullDto updateBubiOrder(BubiOrderFullDto bubiOrderUpdate) {
        BubiOrder bubiOrder = this.bubiOrderRepository.findById(bubiOrderUpdate.getBubiOrderId()).orElse(null);
        if (bubiOrder == null)
            return null;
        bubiOrderUpdate.update(bubiOrder);
        this.bubiOrderRepository.save(bubiOrder);
        return new BubiOrderFullDto(bubiOrder);
    }

    /**
     * mark a bubi order as returned
     *
     * @param bubiOrderId the id of the bubi order
     * @return the updated bubi order
     */
    public BubiOrderFullDto returnBubiOrder(String bubiOrderId) {
        BubiOrder bubiOrder = this.bubiOrderRepository.findById(bubiOrderId).orElse(null);
        if (bubiOrder == null) return null;
        bubiOrder.setBubiStatus(BubiStatus.RETURNED);
        bubiOrder.setLastChange(new Date());
        return new BubiOrderFullDto(bubiOrderRepository.save(bubiOrder));
    }

    /**
     * marks a bubi order as paid and create the corresponding invoice and invoice lines in alma.
     *
     * @param bubiOrderId the id of the bubi order to be paid
     * @return the updated bubi order
     */
    public BubiOrderFullDto payBubiOrder(String bubiOrderId) {
        BubiOrder bubiOrder = this.bubiOrderRepository.findById(bubiOrderId).orElse(null);
        if (bubiOrder == null) return null;
        Invoice invoice = this.almaInvoiceService.getInvoiceForBubiOrder(bubiOrder);
        invoice = this.almaInvoiceService.saveInvoice(invoice);
        List<InvoiceLine> invoiceLines = this.almaInvoiceService.getInvoiceLinesForBubiOrder(bubiOrder);
        for (InvoiceLine invoiceLine : invoiceLines)
            this.almaInvoiceService.addInvoiceLine(invoice.getId(), invoiceLine);
        this.almaInvoiceService.processInvoice(invoice.getId());
        bubiOrder.setPaymentStatus(PaymentStatus.PAID);
        bubiOrder.setLastChange(new Date());
        return new BubiOrderFullDto(this.bubiOrderRepository.save(bubiOrder));
    }

    /**
     * remove a bubi order line from an order line
     *
     * @param bubiOrderId     the id of the bubi order
     * @param bubiOrderlineId the id of the bubi orderline to be removed
     * @return the updated bubi order
     */
    public BubiOrderFullDto removeOrderLine(String bubiOrderId, String bubiOrderlineId) {
        BubiOrderLine bubiOrderLine = this.bubiOrderLineRepository.findById(bubiOrderlineId).orElse(null);
        BubiOrder bubiOrder = this.bubiOrderRepository.findById(bubiOrderId).orElse(null);
        if (bubiOrderLine != null && bubiOrder != null) {
            bubiOrder.removeOrderline(bubiOrderLine);
            bubiOrderLine.setStatus(BubiStatus.INWORK);
            bubiOrderLine.setBubiOrder(null);
            bubiOrderLine.setAlmaSetId(null);
            bubiOrder.setLastChange(new Date());
            this.bubiOrderLineRepository.save(bubiOrderLine);
            this.bubiOrderRepository.save(bubiOrder);
            if (bubiOrder.getAlmaSetId() != null && !bubiOrder.getAlmaSetId().isEmpty())
                this.almaSetService.removeMemberFromSet(bubiOrder.getAlmaSetId(), bubiOrderLine.getAlmaItemId());
            return new BubiOrderFullDto(bubiOrder);
        }
        return null;
    }

    /**
     * adds a bubi order line to a bubi order
     *
     * @param bubiOrderId     the id of the bubi order
     * @param bubiOrderlineId the id of the  bubi order line to be added
     * @return the updated bubi order
     */
    public BubiOrderFullDto addOrderLine(String bubiOrderId, String bubiOrderlineId) {
        BubiOrderLine bubiOrderLine = this.bubiOrderLineRepository.findById(bubiOrderlineId).orElse(null);
        BubiOrder bubiOrder = this.bubiOrderRepository.findById(bubiOrderId).orElse(null);
        if (bubiOrderLine != null && bubiOrder != null) {
            this.almaSetService.addMemberToSet(bubiOrder.getAlmaSetId(),bubiOrderLine.getAlmaItemId(), bubiOrderLine.getTitle());
            bubiOrder.addBubiOrderLine(bubiOrderLine);
            bubiOrderLine.setBubiOrder(bubiOrder);
            bubiOrder.setLastChange(new Date());
            this.bubiOrderLineRepository.save(bubiOrderLine);
            this.bubiOrderRepository.save(bubiOrder);
            return new BubiOrderFullDto(bubiOrder);
        }
        return null;
    }

    /**
     * duplicates a bubi order line in a bubi order
     *
     * @param bubiOrderId     the id of the bubi order
     * @param bubiOrderlineId the id of the  bubi orderline to be duplicated
     * @return the updated bubi order
     */
    public BubiOrderFullDto duplicateOrderline(String bubiOrderId, String bubiOrderlineId) {
        BubiOrderLine bubiOrderLine = this.bubiOrderLineRepository.findById(bubiOrderlineId).orElse(null);
        BubiOrder bubiOrder = this.bubiOrderRepository.findById(bubiOrderId).orElse(null);
        if (bubiOrderLine != null && bubiOrder != null) {
            BubiOrderLine bubiOrderLineNew = bubiOrder.duplicateOderline(bubiOrderLine);
            this.bubiOrderRepository.save(bubiOrder);
            this.bubiOrderLineRepository.save(bubiOrderLineNew);
            return new BubiOrderFullDto(bubiOrder);
        }
        return null;
    }

    /**
     * creates a new bubi order based on a single order line
     * @param orderName the name of the bubi order (corresponds to the set name in Alma)
     * @param bubiOrderline the order line which will be part of the order
     * @return the created bubi order
     */
    public BubiOrder createNewBubiOrder(String orderName, BubiOrderLine bubiOrderline) {
        long counter = this.bubiOrderRepository.countAllByVendorAccount(bubiOrderline.getVendorAccount()) + 1;
        BubiOrder bubiOrder = new BubiOrder(bubiOrderline.getVendorAccount(), counter);
        bubiOrder.setAlmaSetName(orderName);
        Set set = this.almaSetService.createSet(orderName, bubiOrder.getComment());
        this.almaSetService.addMemberToSet(set.getId(),bubiOrderline.getAlmaItemId(), bubiOrderline.getTitle());
        bubiOrder.setAlmaSetId(set.getId());
        return this.bubiOrderRepository.save(bubiOrder);
    }

    @Async
    public void scanItems(String bubiOrderId) {
        BubiOrder bubiOrder = this.bubiOrderRepository.findById(bubiOrderId).orElse(null);
        if (bubiOrder == null || bubiOrder.getAlmaSetId() == null || bubiOrder.getAlmaSetId().isEmpty()) {
            log.warn(String.format("cannot scan in items from bubi order %s: Order or set is empty/null", bubiOrderId));
        } else {
            java.util.Set<BubiOrderLine> bubiOrderLines = bubiOrder.getBubiOrderLines();
            if (bubiOrderLines == null || bubiOrderLines.size() == 0)
                log.warn(String.format("bubi order %s contains no order lines", bubiOrderId));
            else {
                bubiOrderLines.forEach(
                        entry ->  {
                            Item item = this.almaItemService.findItemByMmsAndItemId(entry.getAlmaMmsId(), entry.getAlmaItemId());
                            if (item != null) {
                                item = this.almaItemService.scanInItemDone(item);
                                item = this.almaItemService.scanInItemHomeLocation(item);
                                item = this.removeTemporaryLocation(item);
                            }
                            log.info("processed item " + item);
                        }
                );
            }
        }
    }

    private Item removeTemporaryLocation(Item item) {
        item.getHoldingData().setInTempLocation(true);
        item.getItemData().setPublicNote("");
        switch (item.getItemData().getLibrary().getValue()) {
            case "E0001": {
                item.getHoldingData().tempLocation(new HoldingDataTempLocation().value("ENP"));
                item.getHoldingData().tempLibrary(new HoldingDataTempLibrary().value("E0001"));
                break;
            }
            case "D0001": {
                item.getHoldingData().tempLocation(new HoldingDataTempLocation().value("DNP"));
                item.getHoldingData().tempLibrary(new HoldingDataTempLibrary().value("D0001"));
                break;
            }
        }
        return this.almaItemService.updateItem(item);
    }
}

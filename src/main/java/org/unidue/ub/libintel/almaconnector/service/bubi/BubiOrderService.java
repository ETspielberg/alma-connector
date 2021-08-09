package org.unidue.ub.libintel.almaconnector.service.bubi;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.acq.*;
import org.unidue.ub.alma.shared.bibs.HoldingDataTempLibrary;
import org.unidue.ub.alma.shared.bibs.HoldingDataTempLocation;
import org.unidue.ub.alma.shared.bibs.Item;
import org.unidue.ub.alma.shared.conf.Member;
import org.unidue.ub.alma.shared.conf.Members;
import org.unidue.ub.alma.shared.conf.Set;
import org.unidue.ub.alma.shared.conf.SetAdditionalInfo;
import org.unidue.ub.libintel.almaconnector.model.bubi.*;
import org.unidue.ub.libintel.almaconnector.model.bubi.dto.BubiOrderBriefDto;
import org.unidue.ub.libintel.almaconnector.model.bubi.dto.BubiOrderFullDto;
import org.unidue.ub.libintel.almaconnector.model.bubi.dto.BubiOrderShortDto;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiData;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrder;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrderLine;
import org.unidue.ub.libintel.almaconnector.repository.BubiDataRepository;
import org.unidue.ub.libintel.almaconnector.repository.BubiOrderLineRepository;
import org.unidue.ub.libintel.almaconnector.repository.BubiOrderRepository;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaInvoiceService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaItemService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaPoLineService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaSetService;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * offers functions around bubi orders
 *
 * @author Eike Spielberg
 * @author eike.spielberg@uni-due.de
 * @version 1.0
 */
@Service
public class BubiOrderService {

    private final BubiOrderRepository bubiOrderRepository;

    private final BubiOrderLineRepository bubiOrderLineRepository;

    private final BubiDataRepository bubiDataRepository;

    private final AlmaPoLineService almaPoLineService;

    private final AlmaInvoiceService almaInvoiceService;

    private final AlmaItemService almaItemService;

    private final AlmaSetService almaSetService;

    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

    private final Logger log = LoggerFactory.getLogger(BubiOrderService.class);

    /**
     * constructor based autowiring of the desired services
     * @param bubiOrderRepository the bubi order repository
     * @param bubiOrderLineRepository the bubi orderline repository
     * @param almaPoLineService the alma po line service
     * @param almaInvoiceService the alma invoices service
     * @param almaItemService the alma item service
     * @param bubiDataRepository the bubi data repository
     */
    public BubiOrderService(
            BubiOrderRepository bubiOrderRepository,
            BubiOrderLineRepository bubiOrderLineRepository,
            AlmaPoLineService almaPoLineService,
            AlmaInvoiceService almaInvoiceService,
            AlmaItemService almaItemService,
            BubiDataRepository bubiDataRepository,
            AlmaSetService almaSetService) {
        this.bubiOrderLineRepository = bubiOrderLineRepository;
        this.bubiOrderRepository = bubiOrderRepository;
        this.almaPoLineService = almaPoLineService;
        this.almaInvoiceService = almaInvoiceService;
        this.almaItemService = almaItemService;
        this.bubiDataRepository = bubiDataRepository;
        this.almaSetService = almaSetService;
    }

    /**
     * retrieves a bubi order by its id
     * @param bubiOrderId the id of the bubi order
     * @return the bubi order
     */
    public BubiOrder getBubiOrder(String bubiOrderId) {
        Optional<BubiOrder> option = this.bubiOrderRepository.findById(bubiOrderId);
        return option.orElse(null);
    }

    /**
     * retrieves a bubi order by its id
     * @param bubiOrderId the id of the bubi order
     * @return the bubi order
     */
    public BubiOrderFullDto getBubiOrderFull(String bubiOrderId) {
        Optional<BubiOrder> option = this.bubiOrderRepository.findById(bubiOrderId);
        return option.map(BubiOrderFullDto::new).orElse(null);
    }

    /**
     * retrieves a list of bubi orders by a given mode
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
     * @param bubiOrder the initial bubi order to be packed
     * @return a list of bubiorders grouping the initial bubi order by the vendor accounts
     */
    public List<BubiOrderFullDto> packBubiOrder(BubiOrder bubiOrder) {
        Hashtable<String, BubiOrder> bubiOrders = new Hashtable<>();
        for (BubiOrderLine bubiOrderLine: bubiOrder.getBubiOrderLines()) {
            String key = bubiOrderLine.getVendorAccount();
            if (bubiOrders.containsKey(key))
                bubiOrders.get(key).addBubiOrderLine(bubiOrderLine);
            else {
                long counter = this.bubiOrderRepository.countAllByVendorAccount(bubiOrderLine.getVendorAccount()) + 1;
                BubiOrder bubiOrderInd = new BubiOrder(bubiOrderLine.getVendorAccount(), counter);
                BubiData bubiData = this.bubiDataRepository.getByVendorAccount(bubiOrderLine.getVendorAccount());
                bubiOrderInd.setVendorId(bubiData.getVendorId());
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
                    order.sortBubiOrderLines();
                    order.calculateTotalPrice();
                    order.getBubiOrderLines().forEach(
                            orderline-> {
                                Member member = new Member().id(orderline.getAlmaItemId()).description(orderline.getCollection() + " " + orderline.getShelfmark());
                                members.addMemberItem(member);
                                orderline.setStatus(BubiStatus.PACKED);
                                orderline.setLastChange(new Date());
                                this.bubiOrderLineRepository.save(orderline);
                            });
                    this.bubiOrderRepository.save(order);
                    String date = new SimpleDateFormat(DATE_FORMAT_NOW).format(new Date());
                    String description = order.getVendorAccount() + " " + date;
                    org.unidue.ub.alma.shared.conf.Set set = new Set()
                            .additionalInfo(new SetAdditionalInfo().value(order.getBubiOrderId()))
                            .description(description)
                            .members(members);
                    almaSetService.createSet(set);
                }
        );
        List<BubiOrderFullDto> orders = new ArrayList<>();
        bubiOrders.values().forEach(entry -> orders.add(new BubiOrderFullDto(entry)));
        return orders;
    }

    /**
     * marks a bubi order and the corresponding bubi order lines as collected, sets the corresponding dates and creates the alma po lines
     * @param bubiOrderId the bubi order to be collected
     * @return the updated bubi order object
     */
    public BubiOrderFullDto collectBubiOrder(String bubiOrderId) {
        BubiOrder bubiOrder = this.bubiOrderRepository.findById(bubiOrderId).orElse(null);
        if (bubiOrder == null) return null;
        LocalDate today = LocalDate.now();
        LocalDate returnDate = today.plusDays(21);
        ZoneId defaultZoneId = ZoneId.systemDefault();
        bubiOrder.setCollectedOn(Date.from(today.atStartOfDay(defaultZoneId).toInstant()));
        bubiOrder.setReturnedOn(Date.from(returnDate.atStartOfDay(defaultZoneId).toInstant()));
        for (BubiOrderLine bubiOrderLine : bubiOrder.getBubiOrderLines()) {
            PoLine poLine = almaPoLineService.buildPoLine(bubiOrderLine, returnDate);
            poLine = almaPoLineService.savePoLine(poLine);
            setTemporaryLocation(bubiOrderLine);
            bubiOrderLine.setAlmaPoLineId(poLine.getNumber());
            bubiOrderLine.setStatus(BubiStatus.AT_BUBI);
            bubiOrderLine.setLastChange(new Date());
            this.bubiOrderLineRepository.save(bubiOrderLine);
        }
        bubiOrder.setBubiStatus(BubiStatus.AT_BUBI);
        return new BubiOrderFullDto(bubiOrderRepository.save(bubiOrder));
    }

    /**
     * mark a bubi order as returned
     * @param bubiOrderId the id of the bubi order
     * @return the updated bubi order
     */
    public BubiOrderFullDto returnBubiOrder(String bubiOrderId) {
        BubiOrder bubiOrder = this.bubiOrderRepository.findById(bubiOrderId).orElse(null);
        if (bubiOrder == null) return null;
        bubiOrder.setBubiStatus(BubiStatus.RETURNED);
        return new BubiOrderFullDto(bubiOrderRepository.save(bubiOrder));
    }

    /**
     * marks a bubi order as paid and create the corresponding invoice and invoice lines in alma.
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
        return new BubiOrderFullDto(this.bubiOrderRepository.save(bubiOrder));
    }

    /**
     * remove a bubi order line from an order line
     * @param bubiOrderId the id of the bubi order
     * @param bubiOrderlineId the id of the bubi orderline to be removed
     * @return the updated bubi order
     */
    public BubiOrderFullDto removeOrderLine(String bubiOrderId, String bubiOrderlineId) {
        BubiOrderLine bubiOrderLine = this.bubiOrderLineRepository.findById(bubiOrderlineId).orElse(null);
        BubiOrder bubiOrder = this.bubiOrderRepository.findById(bubiOrderId).orElse(null);
        if (bubiOrderLine != null && bubiOrder != null) {
            bubiOrder.removeOrderline(bubiOrderLine);
            this.bubiOrderRepository.save(bubiOrder);
            return new BubiOrderFullDto(bubiOrder);
        }
        return null;
    }

    /**
     * adds a bubi order line to a bubi order
     * @param bubiOrderId the id of the bubi order
     * @param bubiOrderlineId the id of the  bubi order line to be added
     * @return the updated bubi order
     */
    public BubiOrderFullDto addOrderLine(String bubiOrderId, String bubiOrderlineId) {
        BubiOrderLine bubiOrderLine = this.bubiOrderLineRepository.findById(bubiOrderlineId).orElse(null);
        BubiOrder bubiOrder = this.bubiOrderRepository.findById(bubiOrderId).orElse(null);
        if (bubiOrderLine != null && bubiOrder != null) {
            this.almaSetService.addMemberToSet(bubiOrderLine.getAlmaItemId(), bubiOrder.getAlmaSetId());
            bubiOrder.addBubiOrderLine(bubiOrderLine);
            this.bubiOrderRepository.save(bubiOrder);
            return new BubiOrderFullDto(bubiOrder);
        }
        return null;
    }

    /**
     * duplicates a bubi order line in a bubi order
     * @param bubiOrderId the id of the bubi order
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

    private void setTemporaryLocation(BubiOrderLine bubiOrderLine) {
        Item item = almaItemService.findItemByMmsAndItemId(bubiOrderLine.getAlmaMmsId(), bubiOrderLine.getAlmaItemId());
        item.getHoldingData().setInTempLocation(true);
        item.getItemData().setPublicNote("Buchbinder");
        switch (item.getItemData().getLibrary().getValue()) {
            case "E0001": {
                item.getHoldingData().tempLocation(new HoldingDataTempLocation().value("EBB"));
                item.getHoldingData().tempLibrary(new HoldingDataTempLibrary().value("E0001"));
                break;
            }
            case "D0001": {
                item.getHoldingData().tempLocation(new HoldingDataTempLocation().value("DBB"));
                item.getHoldingData().tempLibrary(new HoldingDataTempLibrary().value("D0001"));
                break;
            }
        }
    }

    public BubiOrder createNewBubiOrder(String bubiOrderId, BubiOrderLine bubiOrderline) {
        long counter = this.bubiOrderRepository.countAllByVendorAccount(bubiOrderline.getVendorAccount()) + 1;
        BubiOrder bubiOrder = new BubiOrder(bubiOrderline.getVendorAccount(), counter);
        bubiOrder.setAlmaSetName(bubiOrderId);
        Set set = new Set().name("Bubi " + bubiOrderId).description(bubiOrder.getComment());
        List<Member> setMembers = new ArrayList<>();
        setMembers.add(new Member().id(bubiOrderline.getAlmaItemId()));
        Members members = new Members().member(setMembers);
        set.setMembers(members);
        try {
            set = this.almaSetService.createSet(set);
        } catch (FeignException fe) {
            log.warn("could not connect create set, faild to connect to alma api", fe);
        }
        bubiOrder.setAlmaSetId(set.getId());
        return this.bubiOrderRepository.save(bubiOrder);
    }
}

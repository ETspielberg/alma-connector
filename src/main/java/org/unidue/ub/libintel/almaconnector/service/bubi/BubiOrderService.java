package org.unidue.ub.libintel.almaconnector.service.bubi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.acq.*;
import org.unidue.ub.alma.shared.bibs.HoldingDataTempLibrary;
import org.unidue.ub.alma.shared.bibs.HoldingDataTempLocation;
import org.unidue.ub.alma.shared.bibs.Item;
import org.unidue.ub.libintel.almaconnector.model.bubi.*;
import org.unidue.ub.libintel.almaconnector.repository.BubiDataRepository;
import org.unidue.ub.libintel.almaconnector.repository.BubiOrderLineRepository;
import org.unidue.ub.libintel.almaconnector.repository.BubiOrderRepository;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaInvoiceService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaItemService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaPoLineService;

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
            BubiDataRepository bubiDataRepository) {
        this.bubiOrderLineRepository = bubiOrderLineRepository;
        this.bubiOrderRepository = bubiOrderRepository;
        this.almaPoLineService = almaPoLineService;
        this.almaInvoiceService = almaInvoiceService;
        this.almaItemService = almaItemService;
        this.bubiDataRepository = bubiDataRepository;
    }

    /**
     * retrieves a bubi order by its id
     * @param bubiOrderId the id of the bubi order
     * @return the bubi order
     */
    public BubiOrder getBubiOrder(String bubiOrderId) {
        return this.bubiOrderRepository.getOne(bubiOrderId);
    }

    /**
     * retrieves a list of bubi orders by a given mode
     * @param mode the mode for which the bubi orders are to be retrieved. currently supported:
     *             'all': retrieves all bubi orders from the repository
     *             'sent': retrieves all sent, but not yet returned bubi orders
     *             'complaint': retrieves all bubi orders with complaints
     *             'closed': retrieves all bubi orders which are already closed
     *             in all other cases all active bubi orders are retrieved (all except closed
     * @return
     */
    public List<BubiOrder> getBubiOrders(String mode) {
        switch (mode) {
            case "all":
                return this.bubiOrderRepository.findAll();
            case "sent":
                return this.bubiOrderRepository.findAllByBubiStatusOrderByBubiOrderId(BubiStatus.SENT);
            case "complaint":
                return this.bubiOrderRepository.findAllByBubiStatusOrderByBubiOrderId(BubiStatus.COMPLAINT);
            case "closed":
                return this.bubiOrderRepository.findAllByBubiStatusOrderByBubiOrderId(BubiStatus.CLOSED);
            default: {
                List<BubiOrder> activeOrders = new ArrayList<>();
                activeOrders.addAll(this.bubiOrderRepository.findAllByBubiStatusOrderByBubiOrderId(BubiStatus.AT_BUBI));
                activeOrders.addAll(this.bubiOrderRepository.findAllByBubiStatusOrderByBubiOrderId(BubiStatus.PACKED));
                activeOrders.addAll(this.bubiOrderRepository.findAllByBubiStatusOrderByBubiOrderId(BubiStatus.NEW));
                activeOrders.addAll(this.bubiOrderRepository.findAllByBubiStatusOrderByBubiOrderId(BubiStatus.SENT));
                activeOrders.addAll(this.bubiOrderRepository.findAllByBubiStatusOrderByBubiOrderId(BubiStatus.WAITING));
                activeOrders.addAll(this.bubiOrderRepository.findAllByBubiStatusOrderByBubiOrderId(BubiStatus.COMPLAINT));
                activeOrders.addAll(this.bubiOrderRepository.findAllByBubiStatusOrderByBubiOrderId(BubiStatus.RETURNED));
                activeOrders.addAll(this.bubiOrderRepository.findAllByBubiStatusOrderByBubiOrderId(BubiStatus.READY));
                activeOrders.sort(Comparator.comparing(BubiOrder::getBubiOrderId));
                return activeOrders;
            }
        }
    }

    /**
     * packs a bubi order to be retrieved by the bubi
     * @param bubiOrder the initial bubi order to be packed
     * @return a list of bubiorders grouping the initial bubi order by the vendor accounts
     */
    public List<BubiOrder> packBubiOrder(BubiOrder bubiOrder) {
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
                    this.bubiOrderRepository.save(order);
                    order.setBubiStatus(BubiStatus.NEW);
                    order.sortBubiOrderLines();
                    order.calculateTotalPrice();
                    order.getBubiOrderLines().forEach(
                            orderline-> {
                                orderline.setStatus(BubiStatus.PACKED);
                                orderline.setLastChange(new Date());
                                this.bubiOrderLineRepository.save(orderline);
                            });
                    this.bubiOrderRepository.save(order);
                }
        );
        return new ArrayList<>(bubiOrders.values());
    }

    /**
     * marks a bubi order and the corresponding bubi order lines as collected, sets the corresponding dates and creates the alma po lines
     * @param bubiOrderId the bubi order to be collected
     * @return the updated bubi order object
     */
    public BubiOrder collectBubiOrder(String bubiOrderId) {
        BubiOrder bubiOrder = this.bubiOrderRepository.getOne(bubiOrderId);
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
        return bubiOrderRepository.save(bubiOrder);
    }

    /**
     * mark a bubi order as returned
     * @param bubiOrderId the id of the bubi order
     * @return the updated bubi order
     */
    public BubiOrder returnBubiOrder(String bubiOrderId) {
        BubiOrder bubiOrder = this.bubiOrderRepository.getOne(bubiOrderId);
        bubiOrder.setBubiStatus(BubiStatus.RETURNED);
        return bubiOrderRepository.save(bubiOrder);
    }

    /**
     * marks a bubi order as paid and create the corresponding invoice and invoice lines in alma.
     * @param bubiOrderId the id of the bubi order to be paid
     * @return the updated bubi order
     */
    public BubiOrder payBubiOrder(String bubiOrderId) {
        BubiOrder bubiOrder = this.bubiOrderRepository.getOne(bubiOrderId);
        Invoice invoice = this.almaInvoiceService.getInvoiceForBubiOrder(bubiOrder);
        invoice = this.almaInvoiceService.saveInvoice(invoice);
        List<InvoiceLine> invoiceLines = this.almaInvoiceService.getInvoiceLinesForBubiOrder(bubiOrder);
        for (InvoiceLine invoiceLine : invoiceLines)
            this.almaInvoiceService.addInvoiceLine(invoice.getId(), invoiceLine);
        this.almaInvoiceService.processInvoice(invoice.getId());
        bubiOrder.setPaymentStatus(PaymentStatus.PAID);
        this.bubiOrderRepository.save(bubiOrder);
        return bubiOrder;
    }

    /**
     * remove a bubi order line from an order line
     * @param bubiOrderId the id of the bubi order
     * @param bubiOrderLine the bubi orderline to be removed
     * @return the updated bubi order
     */
    public BubiOrder removeOrderLine(String bubiOrderId, BubiOrderLine bubiOrderLine) {
        BubiOrder bubiOrder = this.bubiOrderRepository.getOne(bubiOrderId);
        bubiOrder.removeOrderline(bubiOrderLine);
        this.bubiOrderRepository.save(bubiOrder);
        return bubiOrder;
    }

    /**
     * adds a bubi order line to a bubi order
     * @param bubiOrderId the id of the bubi order
     * @param bubiOrderLine the bubi order line to be added
     * @return the updated bubi order
     */
    public BubiOrder addOrderLine(String bubiOrderId, BubiOrderLine bubiOrderLine) {
        BubiOrder bubiOrder = this.bubiOrderRepository.getOne(bubiOrderId);
        bubiOrder.addBubiOrderLine(bubiOrderLine);
        this.bubiOrderRepository.save(bubiOrder);
        return bubiOrder;
    }

    /**
     * duplicates a bubi order line in a bubi order
     * @param bubiOrderId the id of the bubi order
     * @param bubiOrderLine the bubi orderline to be duplicated
     * @return the updated bubi order
     */
    public BubiOrder duplicateOrderline(String bubiOrderId, BubiOrderLine bubiOrderLine) {
        BubiOrder bubiOrder = this.bubiOrderRepository.getOne(bubiOrderId);
        BubiOrderLine bubiOrderLineNew = bubiOrder.duplicateOderline(bubiOrderLine);
        this.bubiOrderRepository.save(bubiOrder);
        this.bubiOrderLineRepository.save(bubiOrderLineNew);
        return bubiOrder;
    }

    /**
     * updates the price of a bubi order line in a bubi order and updates the total price of the bubi order
     * @param bubiOrderLineNew the bubi orderline with the updated price
     * @return the updated bubi order
     */
    public BubiOrder changePrice(BubiOrderLine bubiOrderLineNew) {
        BubiOrderLine bubiOrderLine = this.bubiOrderLineRepository.getBubiOrderLineByBubiOrderLineIdOrderByMinting(bubiOrderLineNew.getBubiOrderLineId());
        bubiOrderLine.setPrice(bubiOrderLineNew.getPrice());
        this.bubiOrderLineRepository.save(bubiOrderLine);
        if (bubiOrderLine.getAlmaPoLineId() != null && !bubiOrderLine.getAlmaPoLineId().isEmpty())
            this.almaPoLineService.updatePoLineByBubiOrderLine(bubiOrderLine);
        BubiOrder bubiOrder = bubiOrderLine.getBubiOrder();
        bubiOrder.calculateTotalPrice();
        this.bubiOrderRepository.save(bubiOrder);

        return bubiOrder;
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
}

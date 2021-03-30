package org.unidue.ub.libintel.almaconnector.service.bubi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.acq.*;
import org.unidue.ub.alma.shared.bibs.HoldingDataTempLibrary;
import org.unidue.ub.alma.shared.bibs.HoldingDataTempLocation;
import org.unidue.ub.alma.shared.bibs.Item;
import org.unidue.ub.libintel.almaconnector.model.bubi.*;
import org.unidue.ub.libintel.almaconnector.repository.BubiOrderLineRepository;
import org.unidue.ub.libintel.almaconnector.repository.BubiOrderRepository;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaInvoiceService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaItemService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaPoLineService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class BubiOrderService {

    private final BubiOrderRepository bubiOrderRepository;

    private final BubiOrderLineRepository bubiOrderLineRepository;

    private final AlmaPoLineService almaPoLineService;

    private final AlmaInvoiceService almaInvoiceService;

    private final AlmaItemService almaItemService;

    private final Logger log = LoggerFactory.getLogger(BubiOrderService.class);

    public BubiOrderService(
            BubiOrderRepository bubiOrderRepository,
            BubiOrderLineRepository bubiOrderLineRepository,
            AlmaPoLineService almaPoLineService,
            AlmaInvoiceService almaInvoiceService,
            AlmaItemService almaItemService) {
        this.bubiOrderLineRepository = bubiOrderLineRepository;
        this.bubiOrderRepository = bubiOrderRepository;
        this.almaPoLineService = almaPoLineService;
        this.almaInvoiceService = almaInvoiceService;
        this.almaItemService = almaItemService;
    }

    public BubiOrder getBubiOrder(String bubiOrderId) {
        return this.bubiOrderRepository.getOne(bubiOrderId);
    }

    public List<BubiOrder> getBubiOrders(String mode) {
        switch (mode) {
            case "all":
                return this.bubiOrderRepository.findAll();
            case "sent":
                return this.bubiOrderRepository.findAllByBubiStatus(BubiStatus.SENT);
            case "complaint":
                return this.bubiOrderRepository.findAllByBubiStatus(BubiStatus.COMPLAINT);
            case "closed":
                return this.bubiOrderRepository.findAllByBubiStatus(BubiStatus.CLOSED);
            default: {
                List<BubiOrder> activeOrders = new ArrayList<>();
                activeOrders.addAll(this.bubiOrderRepository.findAllByBubiStatus(BubiStatus.AT_BUBI));
                activeOrders.addAll(this.bubiOrderRepository.findAllByBubiStatus(BubiStatus.PACKED));
                activeOrders.addAll(this.bubiOrderRepository.findAllByBubiStatus(BubiStatus.NEW));
                activeOrders.addAll(this.bubiOrderRepository.findAllByBubiStatus(BubiStatus.SENT));
                activeOrders.addAll(this.bubiOrderRepository.findAllByBubiStatus(BubiStatus.WAITING));
                activeOrders.addAll(this.bubiOrderRepository.findAllByBubiStatus(BubiStatus.COMPLAINT));
                activeOrders.addAll(this.bubiOrderRepository.findAllByBubiStatus(BubiStatus.RETURNED));
                activeOrders.addAll(this.bubiOrderRepository.findAllByBubiStatus(BubiStatus.READY));
                return activeOrders;
            }
        }
    }


    public List<BubiOrder> packBubiOrder(BubiOrder bubiOrder) {
        Hashtable<String, BubiOrder> bubiOrders = new Hashtable<>();
        for (BubiOrderLine bubiOrderLine: bubiOrder.getBubiOrderLines()) {
            String key = bubiOrderLine.getVendorId() + "-" + bubiOrderLine.getVendorAccount();
            if (bubiOrders.containsKey(key))
                bubiOrders.get(key).addBubiOrderLine(bubiOrderLine);
            else {
                long counter = this.bubiOrderRepository.countAllByVendorIdAndVendorAccount(bubiOrderLine.getVendorId(), bubiOrderLine.getVendorAccount()) + 1;
                BubiOrder bubiOrderInd = new BubiOrder(bubiOrderLine.getVendorId(), bubiOrderLine.getVendorAccount(), counter);
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

    public BubiOrder returnBubiOrder(String bubiOrderId) {
        BubiOrder bubiOrder = this.bubiOrderRepository.getOne(bubiOrderId);
        bubiOrder.setBubiStatus(BubiStatus.RETURNED);
        return bubiOrderRepository.save(bubiOrder);
    }

    public BubiOrder payBubiOrder(BubiOrder bubiOrder) {
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

    public BubiOrder removeOrderLine(String bubiOrderId, BubiOrderLine bubiOrderLine) {
        BubiOrder bubiOrder = this.bubiOrderRepository.getOne(bubiOrderId);
        bubiOrder.removeOrderline(bubiOrderLine);
        this.bubiOrderRepository.save(bubiOrder);
        return bubiOrder;
    }

    public BubiOrder duplicateOrderline(String bubiOrderId, BubiOrderLine bubiOrderLine) {
        BubiOrder bubiOrder = this.bubiOrderRepository.getOne(bubiOrderId);
        BubiOrderLine bubiOrderLineNew = bubiOrder.duplicateOderline(bubiOrderLine);
        this.bubiOrderRepository.save(bubiOrder);
        this.bubiOrderLineRepository.save(bubiOrderLineNew);
        return bubiOrder;
    }

    public BubiOrder changePrice(BubiOrderLine bubiOrderLineNew) {
        BubiOrderLine bubiOrderLine = this.bubiOrderLineRepository.getBubiOrderLineByBubiOrderLineId(bubiOrderLineNew.getBubiOrderLineId());
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

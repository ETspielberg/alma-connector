package org.unidue.ub.libintel.almaconnector.model.bubi.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.unidue.ub.alma.shared.acq.Vendor;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiStatus;
import org.unidue.ub.libintel.almaconnector.model.bubi.PaymentStatus;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="bubi_order")
public class BubiOrder {

    @Id
    @Column(name="bubi_order_id")
    private String bubiOrderId;

    @Column(name = "counter")
    private long counter;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "bubiOrder")
    private Set<BubiOrderLine> bubiOrderLines;

    @Column(name="bubi_status")
    @Enumerated(EnumType.STRING)
    private BubiStatus bubiStatus;

    @Column(name="payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(name="comment")
    private String comment;

    @Column(name="invoice_number")
    private String invoiceNumber;

    @Column(name="alma_po_number")
    private String almaPoNumber;

    @Column(name="alma_set_id")
    private String almaSetId;

    @Column(name="alma_set_name")
    private String almaSetName;

    @Column(name="vendor_id")
    private String vendorId;

    @Column(name="vendor_account")
    private String vendorAccount;

    @Column(name="total_amount")
    private Double totalAmount;

    @Transient
    private Vendor bubiData;

    @Column(name="created")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date created;

    @Column(name="last_change")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date lastChange;

    @Column(name="invoice_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date invoiceDate;

    @Column(name="collected_on")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date collectedOn;

    @Column(name="returned_on")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date returnedOn;

    public BubiOrder() {
        this.bubiOrderLines = new HashSet<>();
        this.counter = 0;
        this.vendorAccount = "";
        this.vendorId = "";
    }

    public BubiOrder(long counter, BubiOrderLine bubiOrderLine) {
        this.vendorId = bubiOrderLine.getVendorId();
        this.vendorAccount = bubiOrderLine.getVendorAccount();
        this.counter = counter;
        this.bubiOrderId = bubiOrderLine.getVendorAccount() + "-" + counter;
        this.bubiOrderLines = new HashSet<>();
        this.bubiOrderLines.add(bubiOrderLine);
        this.bubiStatus = BubiStatus.NEW;
        this.paymentStatus = PaymentStatus.OPEN;
        this.created = new Date();
        this.lastChange = new Date();
        this.totalAmount = bubiOrderLine.getPrice();
    }

    public BubiOrder(String vendorAccount, long counter) {
        this.bubiOrderId = vendorAccount + "-" + counter;
        this.counter = counter;
        this.vendorId = vendorId;
        this.vendorAccount = vendorAccount;
        this.bubiStatus = BubiStatus.NEW;
        this.paymentStatus = PaymentStatus.OPEN;
        this.created = new Date();
        this.lastChange = new Date();
        this.totalAmount = 0.0;
        this.bubiOrderLines = new HashSet<>();
    }

    public BubiOrder withCounter(long counter) {
        this.counter = counter;
        return this;
    }

    public String getBubiOrderId() {
        return bubiOrderId;
    }


    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public void setBubiOrderId(String bubiOrderId) {
        this.bubiOrderId = bubiOrderId;
    }

    public Vendor getBubiData() {
        return bubiData;
    }

    public void setBubiData(Vendor bubiData) {
        this.bubiData = bubiData;
    }

    public Set<BubiOrderLine> getBubiOrderLines() {
        return bubiOrderLines;
    }

    public void setBubiOrderLines(Set<BubiOrderLine> bubiOrderLines) {
        this.bubiOrderLines = bubiOrderLines;
        for (BubiOrderLine bubiOrderLine: bubiOrderLines)
            this.totalAmount += bubiOrderLine.getPrice();
    }

    public void addBubiOrderLine(BubiOrderLine bubiOrderLine) {
        this.bubiOrderLines.add(bubiOrderLine);
        bubiOrderLine.setBubiOrder(this);
        this.totalAmount += bubiOrderLine.getPrice();
    }

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    public String getVendorAccount() {
        return vendorAccount;
    }

    public void setVendorAccount(String vendorAccount) {
        this.vendorAccount = vendorAccount;
    }

    public Date getCollectedOn() {
        return collectedOn;
    }

    public void setCollectedOn(Date collectedOn) {
        this.collectedOn = collectedOn;
    }

    public Date getReturnedOn() {
        return returnedOn;
    }

    public void setReturnedOn(Date returnedOn) {
        this.returnedOn = returnedOn;
    }

    public BubiStatus getBubiStatus() {
        return bubiStatus;
    }

    public void setBubiStatus(BubiStatus bubiStatus) {
        this.bubiStatus = bubiStatus;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAlmaPoNumber() {
        return almaPoNumber;
    }

    public void setAlmaPoNumber(String almaPoLineNumber) {
        this.almaPoNumber = almaPoLineNumber;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastChange() {
        return lastChange;
    }

    public void setLastChange(Date lastChange) {
        this.lastChange = lastChange;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getAlmaSetId() {
        return almaSetId;
    }

    public void setAlmaSetId(String almaSetId) {
        this.almaSetId = almaSetId;
    }

    public String getAlmaSetName() {
        return almaSetName;
    }

    public void setAlmaSetName(String almaSetName) {
        this.almaSetName = almaSetName;
    }

    public Map<String, List<BubiOrderLine>> returnOrderLinesByMediatype() {
        Map<String, List<BubiOrderLine>> typedOrderlines = new HashMap<>();
        typedOrderlines.put("standard", new ArrayList<>());
        typedOrderlines.put("book", new ArrayList<>());
        typedOrderlines.put("journal", new ArrayList<>());
        for (BubiOrderLine bubiOrderLine: this.bubiOrderLines) {
            if (bubiOrderLine.getStandard())
                typedOrderlines.get("standard").add(bubiOrderLine);
            else
                typedOrderlines.get(bubiOrderLine.getMediaType()).add(bubiOrderLine);
        }
        return typedOrderlines;
    }

    public List<BubiOrderLine> collectMonographOrderLines() {
        List<BubiOrderLine> monographOrderLines = new ArrayList<>();
        for (BubiOrderLine bubiOrderLine: this.bubiOrderLines) {
            if ("book".equals(bubiOrderLine.getMediaType()))
                monographOrderLines.add(bubiOrderLine);
        }
        return monographOrderLines;
    }

    public double calculateTotalPrice() {
        this.totalAmount = 0.00;
        for (BubiOrderLine bubiOrderLine: bubiOrderLines) {
            this.totalAmount += bubiOrderLine.getPrice();
        }
        return this.totalAmount;
    }

    public void sortBubiOrderLines() {
        List<BubiOrderLine> orderlines = new ArrayList<>(this.bubiOrderLines);
        orderlines.sort(Comparator.comparing(BubiOrderLine::getMediaType));
        for (int i = 0; i < orderlines.size(); i++)
            orderlines.get(i).setPositionalNumber(i+1);
        this.bubiOrderLines.addAll(orderlines);
    }

    public void removeOrderline(BubiOrderLine bubiOrderLine) {
        this.bubiOrderLines.remove(bubiOrderLine);
    }

    public BubiOrderLine duplicateOderline(BubiOrderLine bubiOrderLine) {
        BubiOrderLine bubiOrderLineNew = bubiOrderLine.clone();
        this.bubiOrderLines.add(bubiOrderLineNew);
        return bubiOrderLineNew;

    }
}

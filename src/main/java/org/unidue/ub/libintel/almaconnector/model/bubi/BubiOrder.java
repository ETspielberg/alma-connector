package org.unidue.ub.libintel.almaconnector.model.bubi;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.unidue.ub.alma.shared.acq.Vendor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="bubi_order")
public class BubiOrder {

    @Id
    @Column(name="bubi_order_id")
    private String bubiOrderId;

    @Column(name = "counter")
    private long counter;

    @Column(name="alma_order_id")
    private String almaOrderId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "bubiOrder")
    private List<BubiOrderLine> bubiOrderLines;

    @Column(name="bubi_status")
    @Enumerated(EnumType.STRING)
    private BubiStatus bubiStatus;

    @Column(name="payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(name="comment")
    private String comment;

    @Column(name="alma_po_line_number")
    private String almaPoLineNumber;

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

    @Column(name="collected_on")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date collectedOn;

    @Column(name="returned_on")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date returnedOn;

    public BubiOrder() {
        this.bubiOrderLines = new ArrayList<>();
        this.counter = 0;
        this.vendorAccount = "";
        this.vendorId = "";
    }

    public BubiOrder(String vendorId, String vendorAccount, long counter) {
        this.vendorId = vendorId;
        this.vendorAccount = vendorAccount;
        this.counter = counter;
        this.bubiOrderLines = new ArrayList<>();
    }

    public BubiOrder(BubiOrderLine bubiOrderLine) {
        this.vendorId = bubiOrderLine.getVendorId();
        this.vendorAccount = bubiOrderLine.getVendorAccount();
        this.counter = 0;
        this.bubiOrderLines = new ArrayList<>();
        this.bubiOrderLines.add(bubiOrderLine);
        this.bubiStatus = BubiStatus.NEW;
        this.paymentStatus = PaymentStatus.OPEN;
        this.created = new Date();
        this.lastChange = new Date();
        this.totalAmount = bubiOrderLine.getPrice();
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

    public String getAlmaOrderId() {
        return almaOrderId;
    }

    public void setAlmaOrderId(String almaOrderId) {
        this.almaOrderId = almaOrderId;
    }

    public Vendor getBubiData() {
        return bubiData;
    }

    public void setBubiData(Vendor bubiData) {
        this.bubiData = bubiData;
    }

    public List<BubiOrderLine> getBubiOrderLines() {
        return bubiOrderLines;
    }

    public void setBubiOrderLines(List<BubiOrderLine> bubiOrderLines) {
        this.bubiOrderLines = bubiOrderLines;
        //for (BubiOrderLine bubiOrderLine: bubiOrderLines)
        //    this.totalAmount += bubiOrderLine.getPrice();
    }

    public void addBubiOrderLine(BubiOrderLine bubiOrderLine) {
        this.bubiOrderLines.add(bubiOrderLine);
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

    public String getAlmaPoLineNumber() {
        return almaPoLineNumber;
    }

    public void setAlmaPoLineNumber(String almaPoLineNumber) {
        this.almaPoLineNumber = almaPoLineNumber;
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

    public double calculateTotalPrice() {
        for (BubiOrderLine bubiOrderLine: bubiOrderLines) {
            this.totalAmount += bubiOrderLine.getPrice();
        }
        return this.totalAmount;
    }
}

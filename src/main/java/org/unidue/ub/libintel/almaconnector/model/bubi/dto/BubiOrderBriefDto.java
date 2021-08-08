package org.unidue.ub.libintel.almaconnector.model.bubi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrder;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrderLine;
import java.util.Date;
import java.util.List;

public class BubiOrderBriefDto {

    private String bubiOrderId;

    private String name;

    private List<BubiOrderLine> bubiOrderLines;

    private String bubiStatus;

    private String paymentStatus;

    private String comment;

    private String almaSetId;

    private String vendorId;

    private String vendorAccount;

    private Double totalAmount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date created;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date lastChange;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date invoiceDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date collectedOn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date returnedOn;

    public BubiOrderBriefDto() {}

    public BubiOrderBriefDto(BubiOrder bubiOrder) {
        this.bubiOrderLines = bubiOrder.getBubiOrderLines();
        this.paymentStatus = bubiOrder.getPaymentStatus().name();
        this.bubiStatus = bubiOrder.getBubiStatus().name();
        this.bubiOrderId = bubiOrder.getBubiOrderId();
        this.comment = bubiOrder.getComment();
        this.vendorAccount = bubiOrder.getVendorAccount();
        this.vendorId = bubiOrder.getVendorId();
        this.totalAmount = bubiOrder.getTotalAmount();
        this.lastChange = bubiOrder.getLastChange();
        this.created = bubiOrder.getCreated();
        this.invoiceDate = bubiOrder.getInvoiceDate();
        this.collectedOn = bubiOrder.getCollectedOn();
        this.returnedOn = bubiOrder.getReturnedOn();
        if (bubiOrder.getAlmaSetName() == null)
            this.name = "";
        else {
            this.name = bubiOrder.getAlmaSetName();
            this.almaSetId = bubiOrder.getAlmaSetId();
        }
    }

    public String getBubiOrderId() {
        return bubiOrderId;
    }

    public void setBubiOrderId(String bubiOrderId) {
        this.bubiOrderId = bubiOrderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVendorAccount() {
        return vendorAccount;
    }

    public void setVendorAccount(String vendorAccount) {
        this.vendorAccount = vendorAccount;
    }

    public List<BubiOrderLine> getBubiOrderLines() {
        return bubiOrderLines;
    }

    public void setBubiOrderLines(List<BubiOrderLine> bubiOrderLines) {
        this.bubiOrderLines = bubiOrderLines;
    }

    public String getBubiStatus() {
        return bubiStatus;
    }

    public void setBubiStatus(String bubiStatus) {
        this.bubiStatus = bubiStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAlmaSetId() {
        return almaSetId;
    }

    public void setAlmaSetId(String almaSetId) {
        this.almaSetId = almaSetId;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
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

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
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
}

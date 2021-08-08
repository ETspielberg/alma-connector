package org.unidue.ub.libintel.almaconnector.model.bubi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrder;

import java.util.*;

public class BubiOrderFullDto {

    private String bubiOrderId;

    private long counter;

    private List<BubiOrderLineFullDto> bubiOrderLines;

    private String bubiStatus;

    private String paymentStatus;

    private String comment;

    private String invoiceNumber;

    private String almaPoNumber;

    private String almaSetId;

    private String almaSetName;

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

    public BubiOrderFullDto() {
    }

    public BubiOrderFullDto(BubiOrder bubiOrder) {
        this.vendorId = bubiOrder.getVendorId();
        this.vendorAccount = bubiOrder.getVendorAccount();
        this.counter = bubiOrder.getCounter();
        this.bubiOrderId = bubiOrder.getBubiOrderId();
        bubiOrder.getBubiOrderLines().forEach(entry -> this.bubiOrderLines.add(new BubiOrderLineFullDto(entry)));
        this.bubiStatus = bubiOrder.getBubiStatus().name();
        this.paymentStatus = bubiOrder.getPaymentStatus().name();
        this.created = bubiOrder.getCreated();
        this.lastChange = bubiOrder.getLastChange();
        this.totalAmount = bubiOrder.getTotalAmount();
        this.comment = bubiOrder.getComment();
        this.invoiceNumber = bubiOrder.getInvoiceNumber();
        this.invoiceDate = bubiOrder.getInvoiceDate();
        this.returnedOn = bubiOrder.getReturnedOn();
        this.collectedOn = bubiOrder.getCollectedOn();
        this.almaPoNumber = bubiOrder.getAlmaPoNumber();
        this.almaSetId = bubiOrder.getAlmaSetId();
        this.almaSetName = bubiOrder.getAlmaSetName();
    }

    public String getBubiOrderId() {
        return bubiOrderId;
    }

    public void setBubiOrderId(String bubiOrderId) {
        this.bubiOrderId = bubiOrderId;
    }

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    public List<BubiOrderLineFullDto> getBubiOrderLines() {
        return bubiOrderLines;
    }

    public void setBubiOrderLines(List<BubiOrderLineFullDto> bubiOrderLines) {
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

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
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

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorAccount() {
        return vendorAccount;
    }

    public void setVendorAccount(String vendorAccount) {
        this.vendorAccount = vendorAccount;
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

    public String getAlmaPoNumber() {
        return almaPoNumber;
    }

    public void setAlmaPoNumber(String almaPoNumber) {
        this.almaPoNumber = almaPoNumber;
    }
}

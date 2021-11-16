package org.unidue.ub.libintel.almaconnector.model.bubi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrder;

import java.time.LocalDate;
import java.util.*;

@Data
public class BubiOrderFullDto {

    private String bubiOrderId;

    private long counter;

    private Set<BubiOrderLineBriefDto> bubiOrderLines;

    private String bubiStatus;

    private String paymentStatus;

    private String comment;

    private String invoiceNumber;

    private String almaPoNumber;

    private String almaSetId;

    private String almaSetName;

    private String vendorAccount;

    private Double totalAmount;

    private String campus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate created;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastChange;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate invoiceDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate collectedOn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate returnedOn;

    private double additionalCosts;

    private String additionalCostsComment;

    private String additionalCostsFund;

    public String mediaType;

    public BubiOrderFullDto() {
    }

    /**
     * creates a full bubi order data transfer object from a BubiOrder object
     *
     * @param bubiOrder the BubiOrder object
     */
    public BubiOrderFullDto(BubiOrder bubiOrder) {
        this.vendorAccount = bubiOrder.getVendorAccount();
        this.counter = bubiOrder.getCounter();
        this.bubiOrderId = bubiOrder.getBubiOrderId();
        this.bubiOrderLines = new HashSet<>();
        bubiOrder.getBubiOrderLines().forEach(entry -> this.bubiOrderLines.add(new BubiOrderLineBriefDto(entry)));
        this.bubiStatus = bubiOrder.getBubiStatus().name();
        this.paymentStatus = bubiOrder.getPaymentStatus().name();
        this.created = bubiOrder.getCreated();
        this.lastChange = bubiOrder.getLastChange();
        this.totalAmount = 0.0;
        bubiOrder.getBubiOrderLines().forEach(entry -> this.totalAmount += entry.getPrice());
        this.comment = bubiOrder.getComment();
        this.invoiceNumber = bubiOrder.getInvoiceNumber();
        this.invoiceDate = bubiOrder.getInvoiceDate();
        this.returnedOn = bubiOrder.getReturnedOn();
        this.collectedOn = bubiOrder.getCollectedOn();
        this.almaPoNumber = bubiOrder.getAlmaPoNumber();
        this.almaSetId = bubiOrder.getAlmaSetId();
        this.almaSetName = bubiOrder.getAlmaSetName();
        this.additionalCosts = bubiOrder.getAdditionalCosts();
        this.additionalCostsComment = bubiOrder.getAdditionalCostsComment();
        this.additionalCostsFund = bubiOrder.getAdditionalCostsFund();
        this.mediaType = bubiOrder.getMediaType();
        if (bubiOrder.getCampus() == null)
            this.campus = "";
        else
            this.campus = bubiOrder.getCampus();
    }

    /**
     * updates the following fields from a submitted BubiOrderFullDto object:
     * - collected on
     * - returnwd
     * - comment
     * - invoice date
     * - invoice number
     * - total amount
     *
     * @param bubiOrder the BubiOrder object to be updated
     */
    public void update(BubiOrder bubiOrder) {
        bubiOrder.setLastChange(LocalDate.now());
        bubiOrder.setCollectedOn(this.collectedOn);
        bubiOrder.setReturnedOn(this.returnedOn);
        bubiOrder.setComment(this.comment);
        bubiOrder.setInvoiceDate(this.invoiceDate);
        bubiOrder.setInvoiceNumber(this.invoiceNumber);
        bubiOrder.setTotalAmount(this.totalAmount);
        bubiOrder.setAdditionalCosts(this.additionalCosts);
        bubiOrder.setAdditionalCostsComment(this.additionalCostsComment);
        bubiOrder.setAdditionalCostsFund(this.additionalCostsFund);
        bubiOrder.setCampus(this.campus);
    }
}

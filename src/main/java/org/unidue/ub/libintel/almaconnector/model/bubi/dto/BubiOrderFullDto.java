package org.unidue.ub.libintel.almaconnector.model.bubi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrder;

import java.util.*;

@Getter
@Setter
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
    }
}

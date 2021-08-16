package org.unidue.ub.libintel.almaconnector.model.bubi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrder;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrderLine;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
public class BubiOrderBriefDto {

    private String bubiOrderId;

    private String name;

    private Set<BubiOrderLine> bubiOrderLines;

    private String bubiStatus;

    private String paymentStatus;

    private String comment;

    private String almaSetId;

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
        this.totalAmount = 0.0;
        bubiOrder.getBubiOrderLines().forEach(entry -> this.totalAmount += entry.getPrice());
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
}

package org.unidue.ub.libintel.almaconnector.model.bubi.dto;

import lombok.Data;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrderLine;

@Data
public class BubiOrderLineBriefDto {

    private String bubiOrderLineId;

    private String collection;

    private String shelfmark;

    private String bubiOrderId;

    private String vendorId;

    private String vendorAccount;

    private String fund;

    private Double price;

    private String title;

    private String mediaType;

    private String bindingsFollow;

    private String status;

    private int bubiOrderlinePositions;

    public BubiOrderLineBriefDto() {}

    public BubiOrderLineBriefDto(BubiOrderLine bubiOrderLine) {
        this.bubiOrderLineId = bubiOrderLine.getBubiOrderLineId();
        this.bindingsFollow = bubiOrderLine.getBindingsFollow();
        try {
            this.bubiOrderId = bubiOrderLine.getBubiOrder().getBubiOrderId();
        } catch (NullPointerException npe) {
            this.bubiOrderId = "";
        }
        if (bubiOrderLine.getBubiOrderlinePositions() == null)
            this.bubiOrderlinePositions = 0;
        else
            this.bubiOrderlinePositions = bubiOrderLine.getBubiOrderlinePositions().size();
        this.collection = bubiOrderLine.getCollection();
        this.fund = bubiOrderLine.getFund();
        this.mediaType = bubiOrderLine.getMediaType();
        this.price = bubiOrderLine.getPrice();
        this.shelfmark = bubiOrderLine.getShelfmark();
        this.status = bubiOrderLine.getStatus().name();
        this.title = bubiOrderLine.getTitle();
        this.vendorAccount = bubiOrderLine.getVendorAccount();
    }
}

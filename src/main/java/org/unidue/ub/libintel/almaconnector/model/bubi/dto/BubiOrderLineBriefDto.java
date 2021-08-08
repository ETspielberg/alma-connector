package org.unidue.ub.libintel.almaconnector.model.bubi.dto;

import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrderLine;

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
        this.vendorId = bubiOrderLine.getVendorId();
    }

    public String getBubiOrderLineId() {
        return bubiOrderLineId;
    }

    public void setBubiOrderLineId(String bubiOrderLineId) {
        this.bubiOrderLineId = bubiOrderLineId;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getShelfmark() {
        return shelfmark;
    }

    public void setShelfmark(String shelfmark) {
        this.shelfmark = shelfmark;
    }

    public String getBubiOrderId() {
        return bubiOrderId;
    }

    public void setBubiOrderId(String bubiOrderId) {
        this.bubiOrderId = bubiOrderId;
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

    public String getFund() {
        return fund;
    }

    public void setFund(String fund) {
        this.fund = fund;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getBindingsFollow() {
        return bindingsFollow;
    }

    public void setBindingsFollow(String bindingsFollow) {
        this.bindingsFollow = bindingsFollow;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getBubiOrderlinePositions() {
        return bubiOrderlinePositions;
    }

    public void setBubiOrderlinePositions(int bubiOrderlinePositions) {
        this.bubiOrderlinePositions = bubiOrderlinePositions;
    }
}

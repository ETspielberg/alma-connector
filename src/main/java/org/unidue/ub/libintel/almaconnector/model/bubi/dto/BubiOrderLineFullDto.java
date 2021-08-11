package org.unidue.ub.libintel.almaconnector.model.bubi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrderLine;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrderlinePosition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class BubiOrderLineFullDto {

    private String bubiOrderLineId;

    private String collection;

    private String shelfmark;

    private long counter;

    private String bubiOrderId;

    private String vendorId;

    private String vendorAccount;

    private String fund;

    private Double price;

    private String almaMmsId;

    private String almaHoldingId;

    private String almaItemId;

    private String title;

    private String precessor;

    private String successor;

    private String minting;

    private String color;

    private String colorMinting;

    private String binding;

    private String cover;

    private String comment;

    private String mediaType;

    @JsonProperty("standard")
    private boolean standard;

    private String bindingsFollow;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date created;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date lastChange;

    private String status;

    private long positionalNumber = -1L;

    private boolean securityStrip = true;

    private boolean mapSlide = false;

    private boolean bindPublisherSleeve = false;

    private boolean coverBack = false;

    private double hours = 0;

    private int numberItems = 1;

    private Set<BubiOrderlinePosition> bubiOrderlinePositions;

    private String internalNote;

    private String bubiNote;

    public BubiOrderLineFullDto() {}

    public BubiOrderLineFullDto(BubiOrderLine bubiOrderLine) {
        this.almaHoldingId = bubiOrderLine.getAlmaHoldingId();
        this.almaMmsId = bubiOrderLine.getAlmaMmsId();
        this.almaItemId = bubiOrderLine.getAlmaItemId();
        this.bubiOrderLineId = bubiOrderLine.getBubiOrderLineId();
        this.binding = bubiOrderLine.getBinding();
        this.bindingsFollow = bubiOrderLine.getBindingsFollow();
        this.bindPublisherSleeve = bubiOrderLine.getBindPublisherSleeve();
        try {
            this.bubiOrderId = bubiOrderLine.getBubiOrder().getBubiOrderId();
        } catch (NullPointerException npe) {
            this.bubiOrderId = "";
        }
        this.bubiOrderlinePositions = bubiOrderLine.getBubiOrderlinePositions();
        this.collection = bubiOrderLine.getCollection();
        this.color = bubiOrderLine.getColor();
        this.colorMinting = bubiOrderLine.getColorMinting();
        this.comment = bubiOrderLine.getComment();
        this.counter = bubiOrderLine.getCounter();
        this.cover = bubiOrderLine.getCover();
        this.coverBack = bubiOrderLine.getCoverBack();
        this.created = bubiOrderLine.getCreated();
        this.fund = bubiOrderLine.getFund();
        this.hours = bubiOrderLine.getHours();
        this.numberItems = bubiOrderLine.getNumberItems();
        this.lastChange = bubiOrderLine.getLastChange();
        this.mapSlide = bubiOrderLine.getMapSlide();
        this.mediaType = bubiOrderLine.getMediaType();
        this.minting = bubiOrderLine.getMinting();
        this.positionalNumber = bubiOrderLine.getPositionalNumber();
        this.precessor = bubiOrderLine.getPrecessor();
        this.price = bubiOrderLine.getPrice();
        this.securityStrip = bubiOrderLine.getSecurityStrip();
        this.shelfmark = bubiOrderLine.getShelfmark();
        this.standard = bubiOrderLine.getStandard();
        this.status = bubiOrderLine.getStatus().name();
        this.successor = bubiOrderLine.getSuccessor();
        this.title = bubiOrderLine.getTitle();
        this.vendorAccount = bubiOrderLine.getVendorAccount();
        this.vendorId = bubiOrderLine.getVendorId();
        this.bubiNote = bubiOrderLine.getBubiNote();
        this.internalNote = bubiOrderLine.getInternalNote();
    }

    public BubiOrderLine updateBubiOrderLine(BubiOrderLine bubiOrderLine) {
        bubiOrderLine.setBinding(this.binding);
        bubiOrderLine.setBindingsFollow(this.bindingsFollow);
        bubiOrderLine.setBindPublisherSleeve(this.bindPublisherSleeve);
        bubiOrderLine.setCollection(this.collection);
        bubiOrderLine.setColor(this.color);
        bubiOrderLine.setColorMinting(this.colorMinting);
        bubiOrderLine.setComment(this.comment);
        bubiOrderLine.setCounter(this.counter);
        bubiOrderLine.setCover(this.cover);
        bubiOrderLine.setCoverBack(this.coverBack);
        bubiOrderLine.setFund(this.fund);
        bubiOrderLine.setHours(this.hours);
        bubiOrderLine.setLastChange(new Date());
        bubiOrderLine.setMapSlide(this.mapSlide);
        bubiOrderLine.setMediaType(this.mediaType);
        bubiOrderLine.setMinting(this.minting);
        bubiOrderLine.setPrecessor(this.precessor);
        bubiOrderLine.setSecurityStrip(this.securityStrip);
        bubiOrderLine.setShelfmark(this.shelfmark);
        bubiOrderLine.setStandard(this.standard);
        bubiOrderLine.setVendorAccount(this.vendorAccount);
        bubiOrderLine.setVendorId(this.vendorId);
        bubiOrderLine.setBubiOrderlinePositions(this.bubiOrderlinePositions);
        bubiOrderLine.setBubiNote(this.bubiNote);
        bubiOrderLine.setInternalNote(this.internalNote);
        return bubiOrderLine;
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

    public String getAlmaMmsId() {
        return almaMmsId;
    }

    public void setAlmaMmsId(String almaMmsId) {
        this.almaMmsId = almaMmsId;
    }

    public String getAlmaHoldingId() {
        return almaHoldingId;
    }

    public void setAlmaHoldingId(String almaHoldingId) {
        this.almaHoldingId = almaHoldingId;
    }

    public String getAlmaItemId() {
        return almaItemId;
    }

    public void setAlmaItemId(String almaItemId) {
        this.almaItemId = almaItemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrecessor() {
        return precessor;
    }

    public void setPrecessor(String precessor) {
        this.precessor = precessor;
    }

    public String getSuccessor() {
        return successor;
    }

    public void setSuccessor(String successor) {
        this.successor = successor;
    }

    public String getMinting() {
        return minting;
    }

    public void setMinting(String minting) {
        this.minting = minting;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColorMinting() {
        return colorMinting;
    }

    public void setColorMinting(String colorMinting) {
        this.colorMinting = colorMinting;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public boolean getStandard() {
        return standard;
    }

    public void setStandard(boolean standard) {
        this.standard = standard;
    }

    public String getBindingsFollow() {
        return bindingsFollow;
    }

    public void setBindingsFollow(String bindingsFollow) {
        this.bindingsFollow = bindingsFollow;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getPositionalNumber() {
        return positionalNumber;
    }

    public void setPositionalNumber(long positionalNumber) {
        this.positionalNumber = positionalNumber;
    }

    public boolean getSecurityStrip() {
        return securityStrip;
    }

    public void setSecurityStrip(boolean securityStrip) {
        this.securityStrip = securityStrip;
    }

    public boolean getMapSlide() {
        return mapSlide;
    }

    public void setMapSlide(boolean mapSlide) {
        this.mapSlide = mapSlide;
    }

    public boolean getBindPublisherSleeve() {
        return bindPublisherSleeve;
    }

    public void setBindPublisherSleeve(boolean bindPublisherSleeve) {
        this.bindPublisherSleeve = bindPublisherSleeve;
    }

    public boolean getCoverBack() {
        return coverBack;
    }

    public void setCoverBack(boolean coverBack) {
        this.coverBack = coverBack;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public int getNumberItems() {
        return numberItems;
    }

    public void setNumberItems(int numberItems) {
        this.numberItems = numberItems;
    }

    public Set<BubiOrderlinePosition> getBubiOrderlinePositions() {
        return bubiOrderlinePositions;
    }

    public void setBubiOrderlinePositions(Set<BubiOrderlinePosition> bubiOrderlinePositions) {
        this.bubiOrderlinePositions = bubiOrderlinePositions;
    }

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    public String getInternalNote() {
        return internalNote;
    }

    public void setInternalNote(String internalNote) {
        this.internalNote = internalNote;
    }

    public String getBubiNote() {
        return bubiNote;
    }

    public void setBubiNote(String bubiNote) {
        this.bubiNote = bubiNote;
    }
}

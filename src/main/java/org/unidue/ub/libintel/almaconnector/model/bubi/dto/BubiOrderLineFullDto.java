package org.unidue.ub.libintel.almaconnector.model.bubi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrderLine;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrderlinePosition;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
public class BubiOrderLineFullDto {

    private String bubiOrderLineId;

    private String collection;

    private String shelfmark;

    private long counter;

    private String bubiOrderId;

    private String vendorAccount;

    private String fund;

    private Double price;

    private String almaMmsId;

    private String almaHoldingId;

    private String almaItemId;

    private String title;

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

    private String almaPoLineId;

    private boolean newSampleBoardNeeded;

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
        this.price = bubiOrderLine.getPrice();
        this.securityStrip = bubiOrderLine.getSecurityStrip();
        this.shelfmark = bubiOrderLine.getShelfmark();
        this.standard = bubiOrderLine.getStandard();
        this.status = bubiOrderLine.getStatus().name();
        this.title = bubiOrderLine.getTitle();
        this.vendorAccount = bubiOrderLine.getVendorAccount();
        this.bubiNote = bubiOrderLine.getBubiNote();
        this.internalNote = bubiOrderLine.getInternalNote();
        this.newSampleBoardNeeded = bubiOrderLine.getNewSampleBoardNeeded();
        this.almaPoLineId = bubiOrderLine.getAlmaPoLineId();
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
        bubiOrderLine.setSecurityStrip(this.securityStrip);
        bubiOrderLine.setShelfmark(this.shelfmark);
        bubiOrderLine.setStandard(this.standard);
        bubiOrderLine.setVendorAccount(this.vendorAccount);
        bubiOrderLine.setBubiOrderlinePositions(this.bubiOrderlinePositions);
        bubiOrderLine.setBubiNote(this.bubiNote);
        bubiOrderLine.setInternalNote(this.internalNote);
        bubiOrderLine.setAlmaPoLineId(this.almaPoLineId);
        bubiOrderLine.setNewSampleBoardNeeded(this.newSampleBoardNeeded);
        return bubiOrderLine;
    }
}

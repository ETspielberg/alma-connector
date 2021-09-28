package org.unidue.ub.libintel.almaconnector.model.bubi.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.unidue.ub.libintel.almaconnector.model.bubi.dto.AlmaItemData;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiStatus;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "bubi_order_line")
public class BubiOrderLine implements Cloneable, Comparable<BubiOrderLine> {

    @Id
    @Column(name = "bubi_order_line_id")
    private String bubiOrderLineId;

    @Column(name = "collection")
    private String collection;

    @Column(name = "shelfmark")
    private String shelfmark;

    @Column(name = "counter")
    private long counter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bubi_order_id")
    @JsonIgnore
    private BubiOrder bubiOrder;

    @Column(name= "alma_vendor_account")
    private String vendorAccount;

    @Column(name = "alma_poline_id")
    private String almaPoLineId;

    @Column(name = "fund")
    private String fund;

    @NumberFormat(style = NumberFormat.Style.NUMBER, pattern = "#,##")
    @Column(name = "price")
    private Double price;

    @Column(name= "alma_mms_id")
    private String almaMmsId;

    @Column(name = "alma_set_id")
    private String almaSetId;

    @Column(name = "title")
    private String title;

    @Column(name = "minting")
    private String minting;

    @Column(name = "color")
    private String color;

    @Column(name = "color_minting")
    private String colorMinting;

    @Column(name = "binding")
    private String binding;

    @Column(name = "cover")
    private String cover;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "media_type")
    private String mediaType;

    @Column(name = "standard")
    @JsonProperty("standard")
    private boolean standard;

    @Column(name = "bindings_follow")
    private String bindingsFollow;

    @Column(name = "price_correction")
    private double priceCorrection;

    @Column(name = "price_correction_comment")
    private String priceCorrectionComment;

    @Column(name = "created")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date created;

    @Column(name = "last_change")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date lastChange;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private BubiStatus status;

    @Column(name="positional_number")
    private long positionalNumber = -1L;

    @Column(name="security_strip")
    private boolean securityStrip = true;

    @Column(name="map_slide")
    private boolean mapSlide = false;

    @Column(name="map_slide_with_correction")
    private boolean mapSlideWithCorrection = false;

    @Column(name="bind_publisher_sleeve")
    private boolean bindPublisherSleeve = false;

    @Column(name="cover_back")
    private boolean coverBack = false;

    @Column(name="without_removal")
    private boolean withoutRemoval = false;

    @Column(name="preserve_front_pages")
    private boolean preserveFrontPages = false;

    @Column(name="hours")
    private double hours = 0;

    @Column(name= "number_items")
    private int numberItems = 1;

    @Column(name = "internal_note")
    private String internalNote;

    @Column(name = "bubi_note")
    private String bubiNote;

    @Column(name = "new_sample_board_needed")
    private boolean newSampleBoardNeeded = false;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "bubiOrderLine")
    private Set<BubiOrderlinePosition> bubiOrderlinePositions;

    public BubiOrderLine() {
        this.status = BubiStatus.NEW;
        this.lastChange = new Date();
        this.created = new Date();
        this.bubiOrderlinePositions = new HashSet<>();
    }

    public BubiOrderLine(String collection, String shelfmark, long counter) {
        this.status = BubiStatus.NEW;
        this.shelfmark = shelfmark;
        this.collection = collection;
        this.counter = counter;
        this.lastChange = new Date();
        this.created = new Date();
        this.bubiOrderlinePositions = new HashSet<>();
        this.updateBubiOrderLineId();
    }

    public void addCoreData(CoreData coredata, boolean standard) {
        if (coredata == null)
            return;
        this.lastChange = new Date();
        this.binding = coredata.getBinding();
        this.bindingsFollow = coredata.getBindingsFollow();
        this.color = coredata.getColor();
        this.comment = coredata.getComment();
        this.cover = coredata.getCover();
        if (this.mediaType == null)
            this.mediaType = coredata.getMediaType();
        this.standard = standard;
        this.minting = coredata.getMinting();
        if (this.title == null) {
            if (coredata.getTitle() != null)
                this.title = coredata.getTitle();
            else
                this.title = coredata.getMinting();
        }
        if (this.almaMmsId == null)
            this.almaMmsId = coredata.getAlmaMmsId();
        this.securityStrip = coredata.getSecurityStrip();
        this.mapSlide = coredata.getMapSlide();
        this.bindPublisherSleeve = coredata.getBindPublisherSleeve();
        this.coverBack = coredata.getCoverBack();
        this.withoutRemoval = coredata.getWithoutRemoval();
        this.hours = coredata.getHours();
        this.standard = coredata.getStandard();
        this.vendorAccount = coredata.getVendorAccount();
    }

    public void addPositionCoredata(CoreData coredata) {
        BubiOrderlinePosition bubiOrderlinePosition = new BubiOrderlinePosition()
                .withVolume(coredata.getPositionVolume())
                .withDescription(coredata.getPositionDescription())
                .withPart(coredata.getPositionPart())
                .withYear(coredata.getPositionYear())
                .withMmsId(coredata.getAlmaMmsId())
                .withHoldingId(coredata.getAlmaHoldingId());
        this.bubiOrderlinePositions.add(bubiOrderlinePosition);
    }

    public void addAlmaItemData(AlmaItemData almaItemData) {
        this.lastChange = new Date();
        this.title = almaItemData.title;
        this.almaMmsId = almaItemData.mmsId;
    }

    public String getShelfmark() {
        return shelfmark;
    }

    public void setShelfmark(String shelfmark) {
        this.shelfmark = shelfmark;
        this.updateBubiOrderLineId();
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
        this.updateBubiOrderLineId();
    }

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
        this.updateBubiOrderLineId();
    }

    public long getPositionalNumber() {
        return positionalNumber;
    }

    public void setPositionalNumber(long positionalNumber) {
        this.positionalNumber = positionalNumber;
    }

    public String getAlmaPoLineId() {
        return almaPoLineId;
    }

    public void setAlmaPoLineId(String almaPoLineId) {
        this.almaPoLineId = almaPoLineId;
    }

    public BubiStatus getStatus() {
        return status;
    }

    public void setStatus(BubiStatus status) {
        this.status = status;
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

    public boolean getStandard() {
        return standard;
    }

    public void setStandard(boolean ff) {
        standard = ff;
    }

    public String getAlmaMmsId() {
        return almaMmsId;
    }

    public void setAlmaMmsId(String almaMmsId) {
        this.almaMmsId = almaMmsId;
    }

    public String getBindingsFollow() {
        return bindingsFollow;
    }

    public void setBindingsFollow(String bindingsFollow) {
        this.bindingsFollow = bindingsFollow;
    }

    public BubiOrder getBubiOrder() {
        return bubiOrder;
    }

    public void setBubiOrder(BubiOrder bubiOrder) {
        this.bubiOrder = bubiOrder;
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

    public String getVendorAccount() {
        return vendorAccount;
    }

    public void setVendorAccount(String vendorAccount) {
        this.vendorAccount = vendorAccount;
    }

    public String getBubiOrderLineId() {
        return bubiOrderLineId;
    }

    public void setBubiOrderLineId(String bubiOrderLineId) {
        this.bubiOrderLineId = bubiOrderLineId;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
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

    public void updateBubiOrderLineId() {
        this.bubiOrderLineId = String.format("%s-%s-%d", this.collection, this.shelfmark, this.counter);
    }

    public String getColorMinting() {
        return colorMinting;
    }

    public void setColorMinting(String colorMinting) {
        this.colorMinting = colorMinting;
    }

    public Set<BubiOrderlinePosition> getBubiOrderlinePositions() {
        return bubiOrderlinePositions;
    }

    public void setBubiOrderlinePositions(Set<BubiOrderlinePosition> bubiOrderlinePositions) {
        this.bubiOrderlinePositions = bubiOrderlinePositions;
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

    public boolean getNewSampleBoardNeeded() {
        return newSampleBoardNeeded;
    }

    public void setNewSampleBoardNeeded(boolean newSampleBoardNeeded) {
        this.newSampleBoardNeeded = newSampleBoardNeeded;
    }

    public String getAlmaSetId() {
        return almaSetId;
    }

    public void setAlmaSetId(String almaSetId) {
        this.almaSetId = almaSetId;
    }

    public boolean getWithoutRemoval() {
        return withoutRemoval;
    }

    public void setWithoutRemoval(boolean withoutRemoval) {
        this.withoutRemoval = withoutRemoval;
    }

    public boolean getMapSlideWithCorrection() {
        return mapSlideWithCorrection;
    }

    public void setMapSlideWithCorrection(boolean mapSlideWithCorrection) {
        this.mapSlideWithCorrection = mapSlideWithCorrection;
    }

    public double getPriceCorrection() {
        return priceCorrection;
    }

    public void setPriceCorrection(double priceCorrection) {
        this.priceCorrection = priceCorrection;
    }

    public String getPriceCorrectionComment() {
        return priceCorrectionComment;
    }

    public void setPriceCorrectionComment(String priceCorrectionComment) {
        this.priceCorrectionComment = priceCorrectionComment;
    }

    public boolean getPreserveFrontPages() {
        return preserveFrontPages;
    }

    public void setPreserveFrontPages(boolean preserveFrontPages) {
        this.preserveFrontPages = preserveFrontPages;
    }

    @Override
    public int compareTo(BubiOrderLine other) {
        return (int) (other.counter - this.counter);
    }

    public BubiOrderLine clone() {
        BubiOrderLine clone = new BubiOrderLine();
        clone.setPrice(this.price);
        clone.setBubiOrder(this.bubiOrder);
        clone.setBubiNote(this.bubiNote);
        clone.setBinding(this.binding);
        clone.setInternalNote(this.internalNote);
        clone.setStatus(this.status);
        clone.setAlmaPoLineId(almaPoLineId);
        clone.setAlmaMmsId(almaMmsId);
        clone.setBinding(binding);
        clone.setBindingsFollow(bindingsFollow);
        clone.setBindPublisherSleeve(this.bindPublisherSleeve);
        clone.setBubiOrderlinePositions(this.bubiOrderlinePositions);
        clone.setCollection(this.collection);
        clone.setShelfmark(this.shelfmark);
        clone.setColor(this.color);
        clone.setColorMinting(this.colorMinting);
        clone.setComment(this.comment);
        clone.setCover(this.cover);
        clone.setCoverBack(this.coverBack);
        clone.setWithoutRemoval(this.withoutRemoval);
        clone.setHours(this.hours);
        clone.setCreated(new Date());
        clone.setLastChange(new Date());
        clone.setFund(this.fund);
        clone.setMapSlide(this.mapSlide);
        clone.setMediaType(this.mediaType);
        clone.setNumberItems(this.numberItems);
        clone.setNewSampleBoardNeeded(this.newSampleBoardNeeded);
        clone.setSecurityStrip(this.securityStrip);
        clone.setStandard(this.standard);
        clone.setVendorAccount(this.vendorAccount);
        clone.setTitle(this.title);
        return clone;
    }

    public void addPositions(Set<BubiOrderlinePosition> bubiOrderlinePositions) {
        this.bubiOrderlinePositions.addAll(bubiOrderlinePositions);
    }

    public void addPosition(BubiOrderlinePosition bubiOrderlinePosition) {
        this.bubiOrderlinePositions.add(bubiOrderlinePosition);
    }

    public void addComment(String comment) {
        if (this.comment == null || this.comment.isEmpty())
            this.comment = comment;
        else
            this.comment += comment;
    }
}

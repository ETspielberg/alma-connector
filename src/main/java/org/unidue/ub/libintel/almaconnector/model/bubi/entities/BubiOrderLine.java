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

    @Column(name = "alma_vendor_id")
    private String vendorId;

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

    @Column(name= "alma_holding_id")
    private String almaHoldingId;

    @Column(name = "alma_item_id")
    private String almaItemId;

    @Column(name = "title")
    private String title;

    @Column(name = "precessor")
    private String precessor;

    @Column(name = "successor")
    private String successor;

    @Column(name = "minting")
    private String minting;

    @Column(name = "color")
    private String color;

    @Column(name = "color_minting")
    private String colorMinting;

    @Column(name = "binding")
    private String binding;

    @Column(name = "part_description")
    private String partDescription;

    @Column(name = "cover")
    private String cover;

    @Column(name="volume")
    private String volume;

    @Column(name = "part_title")
    private String partTitle;

    @Column(name = "edition")
    private String edition;

    @Column(name = "issue")
    private String issue;

    @Column(name = "year")
    private String year;

    @Column(name = "part")
    private String part;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "media_type")
    private String mediaType;

    @Column(name = "standard")
    @JsonProperty("standard")
    private boolean standard;

    @Column(name = "bindings_follow")
    private String bindingsFollow;

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

    @Column(name="volume_suffix")
    private String volumeSuffix;

    @Column(name="security_strip")
    private boolean securityStrip = true;

    @Column(name="map_slide")
    private boolean mapSlide = false;

    @Column(name="bind_publisher_sleeve")
    private boolean bindPublisherSleeve = false;

    @Column(name="cover_back")
    private boolean coverBack = false;

    @Column(name="hours")
    private double hours = 0;

    @Column(name= "number_items")
    private int numberItems = 1;

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
        this.edition = coredata.getEdition();
        if (this.mediaType == null)
            this.mediaType = coredata.getMediaType();
        this.standard = standard;
        this.minting = coredata.getMinting();
        this.part = coredata.getPart();
        this.partDescription = coredata.getPartDescription();
        if (this.title == null) {
            if (coredata.getTitle() != null)
                this.title = coredata.getTitle();
            else
                this.title = coredata.getMinting();
        }
        this.partTitle = coredata.getPartTitle();
        this.vendorId = coredata.getVendorAccount();
        if (this.almaMmsId == null)
            this.almaMmsId = coredata.getAlmaMmsId();
        if (this.almaHoldingId == null)
            this.almaHoldingId = coredata.getAlmaHoldingId();
        BubiOrderlinePosition bubiOrderlinePosition = new BubiOrderlinePosition()
                .withVolumeSuffix(volumeSuffix);
        this.bubiOrderlinePositions.add(bubiOrderlinePosition);
        this.volumeSuffix = coredata.getVolumeSuffix();
        this.securityStrip = coredata.getSecurityStrip();
        this.mapSlide = coredata.getMapSlide();
        this.bindPublisherSleeve = coredata.getBindPublisherSleeve();
        this.coverBack = coredata.getCoverBack();
        this.hours = coredata.getHours();
        this.standard = coredata.getStandard();
    }

    public void addAlmaItemData(AlmaItemData almaItemData) {
        this.lastChange = new Date();
        this.title = almaItemData.title;
        this.almaMmsId = almaItemData.mmsId;
        this.almaHoldingId = almaItemData.holdingId;
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

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
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

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
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

    public String getPartDescription() {
        return partDescription;
    }

    public void setPartDescription(String partDescription) {
        this.partDescription = partDescription;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getPartTitle() {
        return partTitle;
    }

    public void setPartTitle(String partTitle) {
        this.partTitle = partTitle;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
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

    public String getAlmaHoldingId() {
        return almaHoldingId;
    }

    public void setAlmaHoldingId(String almaHoldingId) {
        this.almaHoldingId = almaHoldingId;
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

    public String getAlmaItemId() {
        return almaItemId;
    }

    public void setAlmaItemId(String almaItemId) {
        this.almaItemId = almaItemId;
    }

    public String getVolumeSuffix() {
        return volumeSuffix;
    }

    public void setVolumeSuffix(String volumeSuffix) {
        this.volumeSuffix = volumeSuffix;
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

    @Override
    public int compareTo(BubiOrderLine other) {
        return (int) (other.counter - this.counter);
    }

    public BubiOrderLine clone() {
        BubiOrderLine clone = new BubiOrderLine();
        clone.setStatus(this.status);
        clone.setBubiOrder(bubiOrder);
        clone.setAlmaItemId(almaItemId);
        clone.setAlmaPoLineId(almaPoLineId);
        clone.setAlmaHoldingId(almaHoldingId);
        clone.setAlmaMmsId(almaMmsId);
        clone.setBinding(binding);
        clone.setBindingsFollow(bindingsFollow);

        return clone;
    }
}

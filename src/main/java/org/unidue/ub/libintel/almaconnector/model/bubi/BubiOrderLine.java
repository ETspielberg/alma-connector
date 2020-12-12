package org.unidue.ub.libintel.almaconnector.model.bubi;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.checkerframework.common.aliasing.qual.Unique;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@IdClass(BubiOrderLineId.class)
@Table(name = "bubi_order_line")
public class BubiOrderLine implements Cloneable, Comparable<BubiOrderLine> {

    @Id
    @Column(name = "collection")
    private String collection;

    @Id
    @Column(name = "shelfmark")
    private String shelfmark;

    @Id
    @Column(name = "counter")
    private long counter;

    @Column(name="bubi_order_line_id")
    @Unique
    private String bubiOrderLineid;

    @ManyToOne
    @JoinColumn(name = "bubi_order_id")
    private BubiOrder bubiOrder;

    @Column(name = "alma_vendor_id")
    private String vendorId;

    @Column(name = "alma_poline_id")
    private String almaPoLineId;

    @Column(name = "fund")
    private String fund;

    @Column(name = "price")
    private Double price;

    @Column(name= "alma_mms_id")
    private String almaMmsId;

    @Column(name= "alma_holding_id")
    private String almaHoldingId;

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

    @Column(name = "is_ff")
    private boolean isFf;

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

    public BubiOrderLine() {
        this.lastChange = new Date();
        this.created = new Date();
    }

    public BubiOrderLine(String collection, String shelfmark, long counter) {
        this.shelfmark = shelfmark;
        this.collection = collection;
        this.counter = counter;
        this.lastChange = new Date();
        this.created = new Date();
    }

    public void addCoreData(CoreData coredata) {
        this.lastChange = new Date();
        this.binding = coredata.getBinding();
        this.bindingsFollow = coredata.getBindingsFollow();
        this.collection = coredata.getCollection();
        this.color = coredata.getColor();
        this.comment = coredata.getComment();
        this.cover = coredata.getCover();
        this.edition = coredata.getEdition();
        this.isFf = coredata.getIsFf();
        this.minting = coredata.getMinting();
        this.issue = coredata.getIssue();
        this.part = coredata.getPart();
        this.partDescription = coredata.getPartDescription();
        this.shelfmark = coredata.getShelfmark();
        this.year = coredata.getYear();
        this.volume = coredata.getVolume();
        this.title = coredata.getTitle();
        this.partTitle = coredata.getPartTitle();
        this.vendorId = coredata.getBubiData();
        this.almaMmsId = coredata.getAlmaMmsId();
        this.almaHoldingId = coredata.getAlmaHoldingId();
    }

    public String getShelfmark() {
        return shelfmark;
    }

    public void setShelfmark(String shelfmark) {
        this.shelfmark = shelfmark;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    public String getAlmaPoLineId() {
        return almaPoLineId;
    }

    public void setAlmaPoLineId(String almaPoLineId) {
        this.almaPoLineId = almaPoLineId;
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

    public String getBubiOrderLineid() {
        return bubiOrderLineid;
    }

    public void setBubiOrderLineid(String bubiOrderLineid) {
        this.bubiOrderLineid = bubiOrderLineid;
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

    public boolean isFf() {
        return isFf;
    }

    public void setFf(boolean ff) {
        isFf = ff;
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


    @Override
    public int compareTo(BubiOrderLine other) {
        return (int) (other.counter - this.counter);
    }
}

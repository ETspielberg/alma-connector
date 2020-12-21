package org.unidue.ub.libintel.almaconnector.model.bubi;

import javax.persistence.*;

@Entity
@Table(name="core_data")
@IdClass(CoreDataId.class)
public class CoreData implements Cloneable {

    @Id
    @Column(name="collection")
    private String collection;

    @Id
    @Column(name="shelfmark")
    private String shelfmark;

    @Column(name="title")
    private String title;

    @Column(name="minting")
    private String minting;

    @Column(name="color")
    private String color;

    @Column(name="binding")
    private String binding;

    @Column(name="part_description")
    private String partDescription;

    @Column(name="volume")
    private String volume;

    @Column(name="cover")
    private String cover;

    @Column(name="part_title")
    private String partTitle;

    @Column(name="edition")
    private String edition;

    @Column(name="issue")
    private String issue;

    @Column(name="year")
    private String year;

    @Column(name="part")
    private String part;

    @Column(name="comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name="is_ff")
    private boolean isFf;

    @Column(name="bindings_follow")
    private String bindingsFollow;

    @Column(name="alma_mms_id")
    private String almaMmsId;

    @Column(name="alma_holding_id")
    private String almaHoldingId;

    @Column(name="active")
    private boolean active = true;

    @Column(name="vendor_id")
    private String vendorId;

    @Column(name="alternative_bubi_data")
    private String alternativeBubiData;

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String bubiData) {
        this.vendorId = bubiData;
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

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean getIsFf() {
        return isFf;
    }

    public void setIsFf(boolean ff) {
        isFf = ff;
    }

    public String getBindingsFollow() {
        return bindingsFollow;
    }

    public void setBindingsFollow(String bindingsFollow) {
        this.bindingsFollow = bindingsFollow;
    }

    public String getAlternativeBubiData() {
        return alternativeBubiData;
    }

    public void setAlternativeBubiData(String alternativeBubiData) {
        this.alternativeBubiData = alternativeBubiData;
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


    public void setFf(boolean ff) {
        isFf = ff;
    }

    @Override
    protected CoreData clone() {
        CoreData clone = new CoreData();
        clone.setAlternativeBubiData(this.alternativeBubiData);
        clone.setVendorId(this.vendorId);
        clone.setBinding(this.binding);
        clone.setBindingsFollow(this.bindingsFollow);
        clone.setCollection(this.collection);
        clone.setColor(this.color);
        clone.setComment(this.comment);
        clone.setCover(this.cover);
        clone.setEdition(this.edition);
        clone.setIsFf(this.isFf);
        clone.setVolume(this.volume);
        clone.setIssue(this.issue);
        clone.setMinting(this.minting);
        clone.setPart(this.part);
        clone.setPartDescription(this.partDescription);
        clone.setPartTitle(this.partTitle);
        clone.setTitle(this.title);
        clone.setYear(this.year);
        clone.setShelfmark(this.shelfmark);
        clone.setAlmaHoldingId(this.almaHoldingId);
        clone.setAlmaMmsId(this.almaMmsId);
        clone.setIsFf(this.isFf);
        return clone;
    }
}

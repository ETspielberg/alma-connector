package org.unidue.ub.libintel.almaconnector.model.bubi.entities;

import javax.persistence.*;

@Entity
@Table(name="core_data")
public class CoreData implements Cloneable {

    @Id
    @Column(name = "core_data_id")
    private String coreDataId;

    @Column(name="collection")
    private String collection;

    @Column(name="shelfmark")
    private String shelfmark;

    @Column(name="title")
    private String title;

    @Column(name="minting")
    private String minting;

    @Column(name="color")
    private String color;

    @Column(name="color_minting")
    private String colorMinting;

    @Column(name="binding")
    private String binding;

    @Column(name="cover")
    private String cover;

    @Column(name="comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name="media_type")
    private String mediaType;

    @Column(name="bindings_follow")
    private String bindingsFollow;

    @Column(name="alma_mms_id")
    private String almaMmsId;

    @Column(name="alma_holding_id")
    private String almaHoldingId;

    @Column(name="active")
    private boolean active = true;

    @Column(name="vendor_id")
    private String vendorAccount;

    @Column(name="standard")
    private boolean standard = false;

    @Column(name="position_volume")
    private String positionVolume;

    @Column(name="position_year")
    private String positionYear;

    @Column(name= "position_part")
    private String positionPart;

    @Column(name= "position_description")
    private String positionDescription;

    @Column(name="security_strip")
    private boolean securityStrip = true;

    @Column(name="map_slide")
    private boolean mapSlide = false;

    @Column(name="without_removal")
    private boolean withoutRemoval = false;

    @Column(name="bind_publisher_sleeve")
    private boolean bindPublisherSleeve = false;

    @Column(name="cover_back")
    private boolean coverBack = false;

    @Column(name = "internal_note")
    private String internalNote = "";

    @Column(name = "bubi_note")
    private String bubiNote = "";

    @Column(name="hours")
    private double hours = 0;

    @Column(name= "fund")
    private String fund;

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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getVendorAccount() {
        return vendorAccount;
    }

    public void setVendorAccount(String bubiData) {
        this.vendorAccount = bubiData;
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

    public String getBindingsFollow() {
        return bindingsFollow;
    }

    public void setBindingsFollow(String bindingsFollow) {
        this.bindingsFollow = bindingsFollow;
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

    public boolean getStandard() {
        return standard;
    }

    public void setStandard(boolean standard) {
        this.standard = standard;
    }

    public String getCoreDataId() {
        return coreDataId;
    }

    public void setCoreDataId(String coreDataId) {
        this.coreDataId = coreDataId;
    }

    public boolean getSecurityStrip() {
        return securityStrip;
    }

    public void setSecurityStrip(boolean secureStrip) {
        this.securityStrip = secureStrip;
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

    public String getColorMinting() {
        return colorMinting;
    }

    public void setColorMinting(String colorMinting) {
        this.colorMinting = colorMinting;
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

    public String getFund() {
        return fund;
    }

    public void setFund(String fund) {
        this.fund = fund;
    }

    public String getPositionVolume() {
        return positionVolume;
    }

    public void setPositionVolume(String positionVolume) {
        this.positionVolume = positionVolume;
    }

    public String getPositionYear() {
        return positionYear;
    }

    public void setPositionYear(String positionYear) {
        this.positionYear = positionYear;
    }

    public String getPositionPart() {
        return positionPart;
    }

    public void setPositionPart(String positionPart) {
        this.positionPart = positionPart;
    }

    public String getPositionDescription() {
        return positionDescription;
    }

    public void setPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
    }

    public boolean getWithoutRemoval() {
        return withoutRemoval;
    }

    public void setWithoutRemoval(boolean withoutRemoval) {
        this.withoutRemoval = withoutRemoval;
    }

    @Override
    public CoreData clone() {
        CoreData clone = new CoreData();
        clone.setVendorAccount(this.vendorAccount);
        clone.setBinding(this.binding);
        clone.setBindingsFollow(this.bindingsFollow);
        clone.setCollection(this.collection);
        clone.setColor(this.color);
        clone.setColorMinting(this.colorMinting);
        clone.setComment(this.comment);
        clone.setCover(this.cover);
        clone.setMediaType(this.mediaType);
        clone.setMinting(this.minting);
        clone.setTitle(this.title);
        clone.setShelfmark(this.shelfmark);
        clone.setAlmaHoldingId(this.almaHoldingId);
        clone.setAlmaMmsId(this.almaMmsId);
        clone.setStandard(this.standard);
        clone.setSecurityStrip(this.securityStrip);
        clone.setMapSlide(this.mapSlide);
        clone.setBindPublisherSleeve(this.bindPublisherSleeve);
        clone.setWithoutRemoval(this.withoutRemoval);
        clone.setCoverBack(this.coverBack);
        clone.setHours(this.hours);
        clone.setInternalNote(this.internalNote);
        clone.setBubiNote(this.bubiNote);
        clone.setPositionDescription(this.positionDescription);
        clone.setPositionPart(this.positionPart);
        clone.setPositionVolume(this.positionVolume);
        clone.setPositionDescription(this.positionDescription);
        return clone;
    }

    public void calculateId() {
        this.coreDataId = this.collection + "-" + this.shelfmark;
    }
}

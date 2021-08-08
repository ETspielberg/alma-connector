package org.unidue.ub.libintel.almaconnector.model.bubi.dto;

import org.unidue.ub.libintel.almaconnector.model.bubi.entities.CoreData;

public class CoreDataFullDto {

    private String coreDataId;

    private String collection;

    private String shelfmark;

    private String title;

    private String minting;

    private String color;

    private String colorMinting;

    private String binding;

    private String partDescription;

    private String cover;

    private String partTitle;

    private String edition;

    private String part;

    private String comment;

    private String mediaType;

    private String bindingsFollow;

    private String almaMmsId;

    private String almaHoldingId;

    private boolean active = true;

    private String vendorAccount;

    private boolean standard = false;

    private String volumeSuffix;

    private boolean securityStrip = true;

    private boolean mapSlide = false;

    private boolean bindPublisherSleeve = false;

    private boolean coverBack = false;

    private double hours = 0;

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

    public String getVendorAccount() {
        return vendorAccount;
    }

    public void setVendorAccount(String bubiData) {
        this.vendorAccount = bubiData;
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

    public String getVolumeSuffix() {
        return volumeSuffix;
    }

    public void setVolumeSuffix(String volumeSuffix) {
        this.volumeSuffix = volumeSuffix;
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

    public CoreDataFullDto() {}

    public CoreDataFullDto(CoreData coreData) {
        this.coreDataId = coreData.getCoreDataId();
        this.collection = coreData.getCollection();
        this.shelfmark = coreData.getShelfmark();
        this.title = coreData.getTitle();
        this.minting = coreData.getMinting();
        this.color = coreData.getColor();
        this.colorMinting = coreData.getColorMinting();
        this.binding = coreData.getBinding();
        this.partDescription = coreData.getPartDescription();
        this.cover = coreData.getCover();
        this.partTitle = coreData.getPartTitle();
        this.edition = coreData.getEdition();
        this.part = coreData.getPart();
        this.comment = coreData.getComment();
        this.mediaType = coreData.getMediaType();
        this.bindingsFollow = coreData.getBindingsFollow();
        this.almaMmsId = coreData.getAlmaMmsId();
        this.almaHoldingId = coreData.getAlmaHoldingId();
        this.active  = coreData.isActive();
        this.vendorAccount = coreData.getVendorAccount();
        this.standard  = coreData.getStandard();
        this.volumeSuffix = coreData.getVolumeSuffix();
        this.securityStrip = coreData.getSecurityStrip();
        this.mapSlide = coreData.getMapSlide();
        this.bindPublisherSleeve = coreData.getBindPublisherSleeve();
        this.coverBack = coreData.getCoverBack();
        this.hours = coreData.getHours();
    }

    public CoreData updateCoreData(CoreData coreData) {
        coreData.setActive(this.active);
        coreData.setCoreDataId(this.coreDataId);
        coreData.setShelfmark(this.shelfmark);
        coreData.setCollection(this.collection);
        coreData.setTitle(this.title);
        coreData.setMinting(this.minting);
        coreData.setBinding(this.binding);
        coreData.setColorMinting(this.colorMinting);
        coreData.setAlmaHoldingId(this.almaHoldingId);
        coreData.setAlmaMmsId(this.almaMmsId);
        coreData.setPartDescription(this.partDescription);
        coreData.setCover(this.cover);
        coreData.setPartTitle(this.partTitle);
        coreData.setEdition(this.edition);
        coreData.setPart(this.part);
        coreData.setComment(this.comment);
        coreData.setMediaType(this.mediaType);
        coreData.setBindingsFollow(this.bindingsFollow);
        coreData.setColor(this.color);
        coreData.setVendorAccount(this.vendorAccount);
        coreData.setStandard(this.standard);
        coreData.setVolumeSuffix(this.volumeSuffix);
        coreData.setSecurityStrip(this.securityStrip);
        coreData.setMapSlide(this.mapSlide);
        coreData.setBindPublisherSleeve(this.bindPublisherSleeve);
        coreData.setCoverBack(this.coverBack);
        coreData.setHours(this.hours);
        return coreData;
    }
}

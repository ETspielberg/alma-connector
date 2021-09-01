package org.unidue.ub.libintel.almaconnector.model.bubi.dto;

import lombok.Data;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.CoreData;

@Data
public class CoreDataFullDto {

    private String coreDataId;

    private String collection;

    private String shelfmark;

    private String title;

    private String minting;

    private String color;

    private String colorMinting;

    private String binding;

    private String positionYear;

    private String positionVolume;

    private String positionPart;

    private String positionDescription;

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

    private String internalNote = "";

    private String bubiNote = "";

    private String fund = "";

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
        this.cover = coreData.getCover();
        this.comment = coreData.getComment();
        this.mediaType = coreData.getMediaType();
        this.bindingsFollow = coreData.getBindingsFollow();
        this.almaMmsId = coreData.getAlmaMmsId();
        this.almaHoldingId = coreData.getAlmaHoldingId();
        this.active  = coreData.isActive();
        this.vendorAccount = coreData.getVendorAccount();
        this.standard  = coreData.getStandard();
        this.securityStrip = coreData.getSecurityStrip();
        this.mapSlide = coreData.getMapSlide();
        this.bindPublisherSleeve = coreData.getBindPublisherSleeve();
        this.coverBack = coreData.getCoverBack();
        this.hours = coreData.getHours();
        this.internalNote = coreData.getInternalNote();
        this.bubiNote = coreData.getBubiNote();
        this.fund = coreData.getFund();
        this.positionVolume = coreData.getPositionVolume();
        this.positionYear = coreData.getPositionYear();
        this.positionPart = coreData.getPositionPart();
        this.positionDescription = coreData.getPositionDescription();
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
        coreData.setCover(this.cover);
        coreData.setComment(this.comment);
        coreData.setMediaType(this.mediaType);
        coreData.setBindingsFollow(this.bindingsFollow);
        coreData.setColor(this.color);
        coreData.setVendorAccount(this.vendorAccount);
        coreData.setStandard(this.standard);
        coreData.setSecurityStrip(this.securityStrip);
        coreData.setMapSlide(this.mapSlide);
        coreData.setBindPublisherSleeve(this.bindPublisherSleeve);
        coreData.setCoverBack(this.coverBack);
        coreData.setHours(this.hours);
        coreData.setBubiNote(this.bubiNote);
        coreData.setInternalNote(this.internalNote);
        coreData.setFund(this.fund);
        coreData.setPositionDescription(this.positionDescription);
        coreData.setPositionVolume(this.positionVolume);
        coreData.setPositionPart(this.positionPart);
        coreData.setPositionYear(this.positionYear);
        return coreData;
    }
}

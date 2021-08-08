package org.unidue.ub.libintel.almaconnector.model.bubi.dto;

import org.unidue.ub.libintel.almaconnector.model.bubi.entities.CoreData;

public class CoreDataBriefDto {

    private String coreDataId;

    private boolean active = true;

    private String collection;

    private String shelfmark;

    private String mediaType;

    private String title;

    private String vendorAccount;

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

    public String getVendorAccount() {
        return vendorAccount;
    }

    public void setVendorAccount(String bubiData) {
        this.vendorAccount = bubiData;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getCoreDataId() {
        return coreDataId;
    }

    public void setCoreDataId(String coreDataId) {
        this.coreDataId = coreDataId;
    }

    public CoreDataBriefDto() {}

    public CoreDataBriefDto(CoreData coreData) {
        this.active = coreData.isActive();
        this.coreDataId = coreData.getCoreDataId();
        this.collection = coreData.getCollection();
        this.shelfmark = coreData.getShelfmark();
        this.mediaType = coreData.getMediaType();
        this.title = coreData.getTitle();
        this.vendorAccount = coreData.getVendorAccount();
    }

}

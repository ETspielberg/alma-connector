package org.unidue.ub.libintel.almaconnector.model.bubi.dto;

import lombok.Getter;
import lombok.Setter;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.CoreData;

@Getter
@Setter
public class CoreDataBriefDto {

    private String coreDataId;

    private boolean active = true;

    private String collection;

    private String shelfmark;

    private String mediaType;

    private String title;

    private String vendorAccount;

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

package org.unidue.ub.libintel.almaconnector.model.usage;

import lombok.Data;
import org.unidue.ub.libintel.almaconnector.model.analytics.RequestsItem;

@Data
public class SingleRequestData {

    private String title;

    private String mmsId;

    private String holdingId;

    private String isbn;

    private String shelfmark;

    private String location;

    private String userGroup;

    private boolean isCald;

    private boolean isMagazin;

    public long nItems = 1L;

    public SingleRequestData(RequestsItem requestsItem) {
        this.mmsId = requestsItem.getMMSId();
        this.holdingId = requestsItem.getHoldingId();
        this.shelfmark = requestsItem.getPermanentCallNumber();
        this.userGroup = requestsItem.getUserGroup();
        this.location = requestsItem.getOwningLibraryCode();
        this.isCald = !requestsItem.getPickupLocation().equals(requestsItem.getOwningLibraryCode());
    }

    public String toString() {
        return "mmsId: " + this.mmsId +
                ", holdingId: " + this.holdingId +
                ", title: " + this.title +
                ", isbn: " + this.isbn +
                ", shelfmark: " + this.shelfmark +
                ", location: " + this.location +
                ", userGroup: " + this.userGroup +
                ", isCald: " + this.isCald +
                ", isMagazin: " + this.isMagazin +
                ", nItems: " + this.nItems;

    }
}

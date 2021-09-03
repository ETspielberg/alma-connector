package org.unidue.ub.libintel.almaconnector.model.usage;

import lombok.Data;
import org.unidue.ub.libintel.almaconnector.model.analytics.RequestsItem;

@Data
public class SingleRequestData {

    private String title;

    private String mmsId;

    private String holdingId;

    private String isbn;

    private String library;

    private String location;

    private String shelfmark;

    private String userGroup;

    public long nItems = 1L;

    public long nRequests = 0L;

    public long nCald = 0L;

    public long nMagazin = 0L;

    public SingleRequestData(RequestsItem requestsItem) {
        this.mmsId = requestsItem.getMMSId();
        this.holdingId = requestsItem.getHoldingId();
        this.shelfmark = requestsItem.getPermanentCallNumber();
        this.location = requestsItem.getOwningLocationName();
        this.userGroup = requestsItem.getUserGroup();
        this.library = requestsItem.getOwningLibraryCode();
    }

    public void addRequest() { this.nRequests++; }

    public void addCald() { this.nCald++; }

    public void addMagazin() { this.nMagazin++; }

    public String toString() {
        return "mmsId: " + this.mmsId +
                ", holdingId: " + this.holdingId +
                ", library: " + this.library +
                ", location: " + this.location +
                ", shelfmark: " + this.shelfmark +
                ", userGroup: " + this.userGroup +
                ", nRequests: " + this.nRequests +
                ", nCald: " + this.nCald +
                ", nMagazin: " + this.nMagazin +
                ", nItems: " + this.nItems +
                ", title: " + this.title +
                ", isbn: " + this.isbn;
    }
}

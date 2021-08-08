package org.unidue.ub.libintel.almaconnector.model.bubi.dto;

public class AlmaItemData {

    public String mmsId = "";

    public String holdingId = "";

    public String collection;

    public String shelfmark;

    public String title = "";

    public String campus = "";

    public String mediaType = "";

    public AlmaItemData(String collection, String shelfmark) {
        this.collection = collection;
        this.shelfmark = shelfmark;
    }

    public AlmaItemData withHoldingId(String holdingId) {
        this.holdingId = holdingId;
        return this;
    }

    public AlmaItemData withMmsId(String mmsId) {
        this.mmsId = mmsId;
        return this;
    }

    public AlmaItemData withTitle(String title) {
        this.title = title;
        return this;
    }

    public AlmaItemData withMediaType(String mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    public AlmaItemData withCampus(String campus) {
        this.campus = campus;
        return this;
    }

    public AlmaItemData withCollection(String collection) {
        this.collection = collection;
        return this;
    }

    public AlmaItemData clone() {
        AlmaItemData clone = new AlmaItemData(this.collection, this.shelfmark)
                .withHoldingId(this.holdingId).withMmsId(this.mmsId).withTitle(title);
        return clone;
    }
}

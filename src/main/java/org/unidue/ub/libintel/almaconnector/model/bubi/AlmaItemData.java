package org.unidue.ub.libintel.almaconnector.model.bubi;

public class AlmaItemData {

    public String mmsId = "";

    public String holdingId = "";

    public String collection;

    public String shelfmark;

    public String title = "";

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

    public AlmaItemData clone() {
        AlmaItemData clone = new AlmaItemData(this.collection, this.shelfmark)
                .withHoldingId(this.holdingId).withMmsId(this.mmsId).withTitle(title);
        return clone;
    }
}

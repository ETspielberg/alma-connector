package org.unidue.ub.libintel.almaconnector.model.bubi;

public class AlmaJournalData {

    public String mmsId = "";

    public String holdingId = "";

    public String collection;

    public String shelfmark;

    public String title = "";

    public AlmaJournalData(String collection, String shelfmark) {
        this.collection = collection;
        this.shelfmark = shelfmark;
    }

    public AlmaJournalData withHoldingId(String holdingId) {
        this.holdingId = holdingId;
        return this;
    }

    public AlmaJournalData withMmsId(String mmsId) {
        this.mmsId = mmsId;
        return this;
    }

    public AlmaJournalData withTitle(String title) {
        this.title = title;
        return this;
    }

    public AlmaJournalData clone() {
        AlmaJournalData clone = new AlmaJournalData(this.collection, this.shelfmark)
                .withHoldingId(this.holdingId).withMmsId(this.mmsId).withTitle(title);
        return clone;
    }
}

package org.unidue.ub.libintel.almaconnector.model.bubi;

import java.io.Serializable;

public class CoreDataId implements Serializable {

    private String collection;

    private String shelfmark;

    public CoreDataId() {
    }

    public CoreDataId(String collection, String shelfmark) {
        this.collection = collection;
        this.shelfmark = shelfmark;
    }

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
}

package org.unidue.ub.libintel.almaconnector.model.bubi;

import java.io.Serializable;

public class BubiOrderLineId implements Serializable {

    private String collection;

    private String shelfmark;

    private long counter=1;

    public BubiOrderLineId() {
    }

    public BubiOrderLineId(String collection, String shelfmark, long counter) {
        this.collection = collection;
        this.shelfmark = shelfmark;
        this.counter = counter;
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

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }
}

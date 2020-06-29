package org.unidue.ub.libintel.almaconnector.model;

import java.util.Date;

public class SapFileMetadata {

    private String filename;

    private Date lastChanged;

    public SapFileMetadata(String filename, Date lastChanged) {
        this.filename = filename;
        this.lastChanged = lastChanged;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Date getLastChanged() {
        return lastChanged;
    }

    public void setLastChanged(Date lastChanged) {
        this.lastChanged = lastChanged;
    }
}

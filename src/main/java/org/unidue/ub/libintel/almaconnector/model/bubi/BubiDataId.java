package org.unidue.ub.libintel.almaconnector.model.bubi;

import java.io.Serializable;

public class BubiDataId implements Serializable {

    private String vendorId;

    private String vendorAccount;

    public BubiDataId() {}

    public BubiDataId(String vendorId, String vendorAccount) {
        this.vendorId = vendorId;
        this.vendorAccount = vendorAccount;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorAccount() {
        return vendorAccount;
    }

    public void setVendorAccount(String vendorAccount) {
        this.vendorAccount = vendorAccount;
    }
}

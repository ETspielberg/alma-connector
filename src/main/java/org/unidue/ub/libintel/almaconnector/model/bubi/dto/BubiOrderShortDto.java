package org.unidue.ub.libintel.almaconnector.model.bubi.dto;

import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrder;

public class BubiOrderShortDto {

    private String bubiOrderId;

    private String name;

    private String vendorAccount;

    public BubiOrderShortDto() {}

    public BubiOrderShortDto(BubiOrder bubiOrder) {
        this.bubiOrderId = bubiOrder.getBubiOrderId();
        this.vendorAccount = bubiOrder.getVendorAccount();
        if (bubiOrder.getAlmaSetName() == null)
            this.name = "";
        else
            this.name = bubiOrder.getAlmaSetName();
    }

    public String getBubiOrderId() {
        return bubiOrderId;
    }

    public void setBubiOrderId(String bubiOrderId) {
        this.bubiOrderId = bubiOrderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVendorAccount() {
        return vendorAccount;
    }

    public void setVendorAccount(String vendorAccount) {
        this.vendorAccount = vendorAccount;
    }
}

package org.unidue.ub.libintel.almaconnector.model.bubi.dto;

import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiData;

public class BubiDataBriefDto {

    private String vendorAccount;

    private String name;

    private boolean active;

    public BubiDataBriefDto() {
    }

    public BubiDataBriefDto(BubiData bubiData) {
        this.vendorAccount = bubiData.getVendorAccount();
        this.name = bubiData.getName();
        this.active = bubiData.getActive();
    }

    public String getVendorAccount() {
        return vendorAccount;
    }

    public void setVendorAccount(String vendorAccount) {
        this.vendorAccount = vendorAccount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

package org.unidue.ub.libintel.almaconnector.model.bubi.dto;

import lombok.Getter;
import lombok.Setter;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiData;

@Getter
@Setter
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
}

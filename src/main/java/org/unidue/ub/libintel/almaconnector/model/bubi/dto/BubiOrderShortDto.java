package org.unidue.ub.libintel.almaconnector.model.bubi.dto;

import lombok.Data;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrder;

@Data
public class BubiOrderShortDto {

    private String bubiOrderId;

    private String name;

    private String vendorAccount;

    private double price;

    public BubiOrderShortDto() {}

    public BubiOrderShortDto(BubiOrder bubiOrder) {
        this.bubiOrderId = bubiOrder.getBubiOrderId();
        this.vendorAccount = bubiOrder.getVendorAccount();
        this.price = bubiOrder.calculateTotalPrice();
        if (bubiOrder.getAlmaSetName() == null)
            this.name = "";
        else
            this.name = bubiOrder.getAlmaSetName();
    }
}

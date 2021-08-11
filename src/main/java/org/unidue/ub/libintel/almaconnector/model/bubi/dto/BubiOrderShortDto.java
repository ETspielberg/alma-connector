package org.unidue.ub.libintel.almaconnector.model.bubi.dto;

import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrder;

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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

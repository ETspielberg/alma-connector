package org.unidue.ub.libintel.almaconnector.model.bubi.dto;


import lombok.Data;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiData;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiPrice;

import java.util.ArrayList;
import java.util.List;

@Data
public class BubiDataFullDto {

    private String vendorId;

    private String vendorAccount;

    private String name;

    private String shortName;

    private String campus;

    private boolean active = true;

    private double priceBindPublisherSleeve = 0.0;

    private double priceCoverBack = 0.0;

    private double priceMapSlide = 0.0;

    private double priceSecurityStrip = 0.0;

    private double pricePerHour = 0.0;

    private List<BubiPrice> prices = new ArrayList<>();

    public BubiDataFullDto() {
    }

    public BubiDataFullDto(BubiData bubiData) {
        this.vendorAccount = bubiData.getVendorAccount();
        this.vendorId = bubiData.getVendorId();
        this.name = bubiData.getName();
        this.shortName = bubiData.getShortName();
        this.campus = bubiData.getCampus();
        this.active = bubiData.getActive();
        this.priceBindPublisherSleeve = bubiData.getPriceBindPublisherSleeve();
        this.priceCoverBack = bubiData.getPriceCoverBack();
        this.priceMapSlide = bubiData.getPriceMapSlide();
        this.priceSecurityStrip = bubiData.getPriceSecurityStrip();
        this.pricePerHour = bubiData.getPricePerHour();
        this.prices = bubiData.getBubiPrices();
    }

    public void updateBubidata(BubiData bubiData) {
        bubiData.setActive(this.active);
        bubiData.setCampus(this.campus);
        bubiData.setName(this.name);
        bubiData.setShortName(this.shortName);
        bubiData.setVendorAccount(this.vendorAccount);
        bubiData.setPriceBindPublisherSleeve(this.priceBindPublisherSleeve);
        bubiData.setPriceCoverBack(this.priceCoverBack);
        bubiData.setPriceMapSlide(this.priceMapSlide);
        bubiData.setPriceSecurityStrip(this.priceSecurityStrip);
        bubiData.setPricePerHour(this.pricePerHour);
        bubiData.setBubiPrices(this.prices);
    }
}

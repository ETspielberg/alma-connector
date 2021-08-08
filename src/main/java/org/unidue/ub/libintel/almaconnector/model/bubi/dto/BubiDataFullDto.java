package org.unidue.ub.libintel.almaconnector.model.bubi.dto;


import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiData;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiPrice;

import java.util.ArrayList;
import java.util.List;

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

    public BubiDataFullDto(){}

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

    public BubiData updateBubidata(BubiData bubiData) {
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
        return bubiData;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getPriceBindPublisherSleeve() {
        return priceBindPublisherSleeve;
    }

    public void setPriceBindPublisherSleeve(double priceBindPublisherSleeve) {
        this.priceBindPublisherSleeve = priceBindPublisherSleeve;
    }

    public double getPriceCoverBack() {
        return priceCoverBack;
    }

    public void setPriceCoverBack(double priceCoverBack) {
        this.priceCoverBack = priceCoverBack;
    }

    public double getPriceMapSlide() {
        return priceMapSlide;
    }

    public void setPriceMapSlide(double priceMapSlide) {
        this.priceMapSlide = priceMapSlide;
    }

    public double getPriceSecurityStrip() {
        return priceSecurityStrip;
    }

    public void setPriceSecurityStrip(double priceSecurityStrip) {
        this.priceSecurityStrip = priceSecurityStrip;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public List<BubiPrice> getPrices() {
        return prices;
    }

    public void setPrices(List<BubiPrice> prices) {
        this.prices = prices;
    }
}

package org.unidue.ub.libintel.almaconnector.model.bubi.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="bubi_data")
public class BubiData {

    @Column(name="vendor_id")
    private String vendorId;

    @Id
    @Column(name="vendor_account")
    private String vendorAccount;

    @Column(name="name")
    private String name;

    @Column(name="short_name")
    private String shortName;

    @Column(name="campus")
    private String campus;

    @Column(name="active")
    private boolean active;

    @Column(name="price_bind_publisher_sleeve")
    private double priceBindPublisherSleeve = 0.0;

    @Column(name="price_cover_back")
    private double priceCoverBack = 0.0;

    @Column(name="price_map_slide")
    private double priceMapSlide = 0.0;

    @Column(name="price_map_slide_with_correction")
    private double priceMapSlideWithCorrection = 0.0;

    @Column(name="price_security_strip")
    private double priceSecurityStrip = 0.0;

    @Column(name="price_per_hour")
    private double pricePerHour = 0.0;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "bubiData")
    private List<BubiPrice> bubiPrices = new ArrayList<>();

    public BubiData() {}

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

    public List<BubiPrice> getBubiPrices() {
        return bubiPrices;
    }

    public void setBubiPrices(List<BubiPrice> bubiPrices) {
        this.bubiPrices = bubiPrices;
    }

    public double getPriceMapSlideWithCorrection() {
        return priceMapSlideWithCorrection;
    }

    public void setPriceMapSlideWithCorrection(double priceMapSlideWithCorrection) {
        this.priceMapSlideWithCorrection = priceMapSlideWithCorrection;
    }

    public BubiPrice retrieveExecutionPrice(String binding, String cover, String materialType) {
        for (BubiPrice price: this.bubiPrices) {
            if (price.getBinding().equals(binding) && price.getCover().equals(cover) && price.getMaterialType().equals(materialType))
                return price;
        }
        return null;
    }
}

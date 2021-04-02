package org.unidue.ub.libintel.almaconnector.model.bubi;

import javax.persistence.*;

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

    @Column(name="standard_price_journal")
    private double standardPriceJournal;

    @Column(name="standard_price_monograph")
    private double standardPriceMonograph;

    @Column(name="additional_costs_amount")
    private double additionalCostsAmount;

    @Column(name="active")
    private boolean active;

    public BubiData() {}

    public BubiData(String vendorId, String name) {
        this.vendorId = vendorId;
        this.name = name;
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

    public double getStandardPriceJournal() {
        return standardPriceJournal;
    }

    public void setStandardPriceJournal(double standardPriceJournal) {
        this.standardPriceJournal = standardPriceJournal;
    }

    public double getStandardPriceMonograph() {
        return standardPriceMonograph;
    }

    public void setStandardPriceMonograph(double standardPriceMonograph) {
        this.standardPriceMonograph = standardPriceMonograph;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getAdditionalCostsAmount() {
        return additionalCostsAmount;
    }

    public void setAdditionalCostsAmount(double additionalCostsAmount) {
        this.additionalCostsAmount = additionalCostsAmount;
    }
}

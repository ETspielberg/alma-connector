package org.unidue.ub.libintel.almaconnector.model.bubi;

import javax.persistence.*;

@Entity
@Table(name="bubi_prices")
public class BubiPrice {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    @Column(name="vendor_account")
    private String vendorAccount;

    @Column(name="price")
    private double price;

    @Column(name="description")
    private String description;

    @Column(name="name")
    private String name;


    public BubiPrice withName(String name) {
        this.name = name;
        return this;
    }

    public BubiPrice withPrice(double price) {
        this.price = price;
        return this;
    }

    public BubiPrice withVendorAccount(String vendorAccount) {
        this.vendorAccount = vendorAccount;
        return this;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

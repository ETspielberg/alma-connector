package org.unidue.ub.libintel.almaconnector.model.bubi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name="bubi_prices")
public class BubiPrice {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    @Column(name="price")
    private double price;

    @Column(name = "material_type")
    private String materialType;

    @Column(name = "cover")
    private String cover;

    @Column(name = "binding")
    private String binding;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_account")
    @JsonIgnore
    private BubiData bubiData;


    public BubiPrice withPrice(double price) {
        this.price = price;
        return this;
    }

    public BubiPrice withMaterialType(String materialType) {
        this.materialType = materialType;
        return this;
    }

    public BubiPrice withBinding(String binding) {
        this.binding = binding;
        return this;
    }

    public BubiPrice withCover(String cover) {
        this.cover = cover;
        return this;
    }

    public BubiPrice withName(String name) {
        this.name = name;
        return this;
    }

    public BubiPrice withBubiData(BubiData bubiData) {
        this.bubiData = bubiData;
        return this;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BubiData getBubiData() {
        return bubiData;
    }

    public void setBubiData(BubiData bubiData) {
        this.bubiData = bubiData;
    }
}

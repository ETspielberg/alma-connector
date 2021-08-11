package org.unidue.ub.libintel.almaconnector.model.bubi.dto;

public class BubiAddress {

    private String name;

    private String street;

    private String city;

    private String plz;

    private String country;

    private String additionaAddressLine;

    public BubiAddress() {}

    public BubiAddress withStreet(String street) {
        this.street = street;
        return this;
    }

    public BubiAddress withCity(String city) {
        this.city = city;
        return this;
    }

    public BubiAddress withPlz(String plz) {
        this.plz = plz;
        return this;
    }

    public BubiAddress withCountry(String country) {
        this.country = country;
        return this;
    }

    public BubiAddress withAdditionalAddressLine(String additionaAddressLine) {
        this.additionaAddressLine = additionaAddressLine;
        return this;
    }

    public BubiAddress withName(String name) {
        this.name = name;
        return this;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPlz() {
        return plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAdditionaAddressLine() {
        return additionaAddressLine;
    }

    public void setAdditionaAddressLine(String additionaAddressLine) {
        this.additionaAddressLine = additionaAddressLine;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

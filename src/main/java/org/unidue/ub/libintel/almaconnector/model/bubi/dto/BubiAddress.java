package org.unidue.ub.libintel.almaconnector.model.bubi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}

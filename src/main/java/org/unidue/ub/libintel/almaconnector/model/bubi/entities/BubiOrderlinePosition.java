package org.unidue.ub.libintel.almaconnector.model.bubi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name= "bubi_orderline_position")
public class BubiOrderlinePosition {

    @Id
    @Column(name= "bubi_orderline_position_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long bubiOrderPositionId;

    @Column(name = "volume")
    private String volume = "";

    @Column(name = "volume_suffix")
    private String volumeSuffix = "";


    @Column(name = "issue")
    private String issue = "";

    @Column(name = "year")
    private String year = "";

    @Column(name = "specification")
    private String specification = "";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bubi_order_line_id")
    @JsonIgnore
    private BubiOrderLine bubiOrderLine;

    public BubiOrderlinePosition withYear(String year) {
        this.year = year;
        return this;
    }

    public BubiOrderlinePosition withVolume(String edition) {
        this.volume = edition;
        return this;
    }

    public BubiOrderlinePosition withSpecification(String specification) {
        this.specification = specification;
        return this;
    }

    public BubiOrderlinePosition withVolumeSuffix(String volumeSuffix) {
        this.volumeSuffix = volumeSuffix;
        return this;
    }

    public long getBubiOrderPositionId() {
        return bubiOrderPositionId;
    }

    public void setBubiOrderPositionId(long bubiOrderPositionId) {
        this.bubiOrderPositionId = bubiOrderPositionId;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String edition) {
        this.volume = edition;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public BubiOrderLine getBubiOrderLine() {
        return bubiOrderLine;
    }

    public void setBubiOrderLine(BubiOrderLine bubiOrderLine) {
        this.bubiOrderLine = bubiOrderLine;
    }

    public String getVolumeSuffix() {
        return volumeSuffix;
    }

    public void setVolumeSuffix(String editionSuffix) {
        this.volumeSuffix = editionSuffix;
    }
}

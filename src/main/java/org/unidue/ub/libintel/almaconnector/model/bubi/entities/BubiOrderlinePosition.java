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

    @Column(name = "part")
    private String part = "";

    @Column(name = "year")
    private String year = "";

    @Column(name = "description")
    private String description = "";

    @Column(name = "bubi_note")
    private String bubiNote = "";

    @Column(name = "internal_note")
    private String internalNote = "";

    @Column(name = "alma_item_id")
    private String almaItemId;

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

    public BubiOrderlinePosition withDescription(String description) {
        this.description = description;
        return this;
    }

    public BubiOrderlinePosition withPart(String part) {
        this.part = part;
        return this;
    }

    public BubiOrderlinePosition withInternalNote(String internalNote) {
        this.internalNote = internalNote;
        return this;
    }

    public BubiOrderlinePosition withBubiNote(String bubiNote) {
        this.bubiNote = bubiNote;
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String specification) {
        this.description = specification;
    }

    public BubiOrderLine getBubiOrderLine() {
        return bubiOrderLine;
    }

    public void setBubiOrderLine(BubiOrderLine bubiOrderLine) {
        this.bubiOrderLine = bubiOrderLine;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String editionSuffix) {
        this.part = editionSuffix;
    }

    public String getBubiNote() {
        return bubiNote;
    }

    public void setBubiNote(String bubiNote) {
        this.bubiNote = bubiNote;
    }

    public String getInternalNote() {
        return internalNote;
    }

    public String getAlmaItemId() {
        return almaItemId;
    }

    public void setAlmaItemId(String almaItemId) {
        this.almaItemId = almaItemId;
    }

    public void setInternalNote(String internalNote) {
        this.internalNote = internalNote;
    }
}

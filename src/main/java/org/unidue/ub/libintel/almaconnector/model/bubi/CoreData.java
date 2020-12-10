package org.unidue.ub.libintel.almaconnector.model.bubi;

import javax.persistence.*;

@Entity
@Table(name="core_data")
public class CoreData implements Cloneable {

    @Id
    @Column(name="core_data_id")
    private String coreDataId;

    @Column(name="collection")
    private String collection;

    @Column(name="shelfmark")
    private String shelfmark;

    @Column(name="title")
    private String title;

    // noch notwendig?
    @Column(name="precessor")
    private String precessor;

    // noch notwendig?
    @Column(name="successor")
    private String successor;

    @Column(name="minting")
    private String minting;

    @Column(name="color")
    private String color;

    @Column(name="binding")
    private String binding;

    @Column(name="part_description")
    private String partDescription;

    @Column(name="volume")
    private String volume;

    @Column(name="cover")
    private String cover;

    @Column(name="part_title")
    private String partTitle;

    @Column(name="edition")
    private String edition;

    @Column(name="issue")
    private String issue;

    @Column(name="year")
    private String year;

    @Column(name="part")
    private String part;

    @Column(name="comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name="is_ff")
    private boolean isFf;

    @Column(name="bindings_follow")
    private String bindingsFollow;

    @Column(name="alma_mms_id")
    private String almaMmsId;

    @Column(name="alma_holding_id")
    private String almaHoldingId;

    @Column(name="bubi_data")
    private String bubiData;

    @Column(name="alternative_bubi_data")
    private String alternativeBubiData;

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getShelfmark() {
        return shelfmark;
    }

    public void setShelfmark(String shelfmark) {
        this.shelfmark = shelfmark;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrecessor() {
        return precessor;
    }

    public void setPrecessor(String precessor) {
        this.precessor = precessor;
    }

    public String getSuccessor() {
        return successor;
    }

    public void setSuccessor(String successor) {
        this.successor = successor;
    }

    public String getMinting() {
        return minting;
    }

    public void setMinting(String minting) {
        this.minting = minting;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public String getPartDescription() {
        return partDescription;
    }

    public void setPartDescription(String partDescription) {
        this.partDescription = partDescription;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getBubiData() {
        return bubiData;
    }

    public void setBubiData(String bubiData) {
        this.bubiData = bubiData;
    }

    public String getPartTitle() {
        return partTitle;
    }

    public void setPartTitle(String partTitle) {
        this.partTitle = partTitle;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
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

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isFf() {
        return isFf;
    }

    public void setFf(boolean ff) {
        isFf = ff;
    }

    public String getBindingsFollow() {
        return bindingsFollow;
    }

    public void setBindingsFollow(String bindingsFollow) {
        this.bindingsFollow = bindingsFollow;
    }

    public String getAlternativeBubiData() {
        return alternativeBubiData;
    }

    public void setAlternativeBubiData(String alternativeBubiData) {
        this.alternativeBubiData = alternativeBubiData;
    }

    public String getCoreDataId() {
        return coreDataId;
    }

    public void setCoreDataId(String coreDataId) {
        this.coreDataId = coreDataId;
    }

    public String getAlmaMmsId() {
        return almaMmsId;
    }

    public void setAlmaMmsId(String almaMmsId) {
        this.almaMmsId = almaMmsId;
    }

    public String getAlmaHoldingId() {
        return almaHoldingId;
    }

    public void setAlmaHoldingId(String almaHoldingId) {
        this.almaHoldingId = almaHoldingId;
    }

    @Override
    protected CoreData clone() {
        CoreData clone = new CoreData();
        clone.setAlternativeBubiData(this.alternativeBubiData);
        clone.setBubiData(this.bubiData);
        clone.setBinding(this.binding);
        clone.setBindingsFollow(this.bindingsFollow);
        clone.setCollection(this.collection);
        clone.setColor(this.color);
        clone.setComment(this.comment);
        clone.setCover(this.cover);
        clone.setEdition(this.edition);
        clone.setFf(this.isFf);
        clone.setVolume(this.volume);
        clone.setIssue(this.issue);
        clone.setMinting(this.minting);
        clone.setPart(this.part);
        clone.setPartDescription(this.partDescription);
        clone.setPartTitle(this.partTitle);
        clone.setPrecessor(this.precessor);
        clone.setSuccessor(this.successor);
        clone.setTitle(this.title);
        clone.setYear(this.year);
        clone.setShelfmark(this.shelfmark);
        clone.setAlmaHoldingId(this.almaHoldingId);
        clone.setAlmaMmsId(this.almaMmsId);
        return clone;
    }
}

package org.unidue.ub.libintel.almaconnector.model.bubi;

import javax.persistence.*;

@Entity
@Table(name="core_data")
public class CoreData {

    @Id
    @Column(name="core_data_id")
    private String coreDataId;

    @Column(name="collection")
    private String collection;

    @Column(name="shelfmark")
    private String shelfmark;

    @Column(name="title")
    private String title;

    @Column(name="precessor")
    private String precessor;

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

    @Column(name="cover")
    private String cover;

    @Column(name="part_title")
    private String partTitle;

    @Column(name="edition")
    private String edition;

    @Column(name="issue")
    private String issue;

    @Column(name="year")
    private int year;

    @Column(name="part")
    private String part;

    @Column(name="comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name="is_ff")
    private boolean isFf;

    @Column(name="bindings_follow")
    private String bindingsFollow;


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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
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
}

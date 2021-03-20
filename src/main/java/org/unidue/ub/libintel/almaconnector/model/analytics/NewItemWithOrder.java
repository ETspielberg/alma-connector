package org.unidue.ub.libintel.almaconnector.model.analytics;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "row")
public class NewItemWithOrder {

    @JacksonXmlProperty(localName = "POLineReference")
    private String poLineReference;

    @JacksonXmlProperty(localName = "PONumber")
    private String poNumber;

    @JacksonXmlProperty(localName = "MMSId")
    private String mmsId;

    @JacksonXmlProperty(localName = "ItemId")
    private String itemId;

    @JacksonXmlProperty(localName = "CreationDate")
    private String creationDate;

    public String getPoLineReference() {
        return poLineReference;
    }

    public void setPoLineReference(String poLineReference) {
        this.poLineReference = poLineReference;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getMmsId() {
        return mmsId;
    }

    public void setMmsId(String mmsId) {
        this.mmsId = mmsId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}

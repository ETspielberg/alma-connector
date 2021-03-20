package org.unidue.ub.libintel.almaconnector.model.analytics;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "row")
public class OpenBubiOrder {

    @JacksonXmlProperty(localName = "POLineReference")
    private String poLineReference;

    @JacksonXmlProperty(localName = "PONumber")
    private String poNumber;

    @JacksonXmlProperty(localName = "POLineTypeName")
    private String poLineTypeName;

    @JacksonXmlProperty(localName = "Status")
    private String status;

    @JacksonXmlProperty(localName = "VendorCode")
    private String vendorCode;

    @JacksonXmlProperty(localName = "VendorName")
    private String vendorName;

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

    public String getPoLineTypeName() {
        return poLineTypeName;
    }

    public void setPoLineTypeName(String poLineTypeName) {
        this.poLineTypeName = poLineTypeName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }
}

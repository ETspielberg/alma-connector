package org.unidue.ub.libintel.almaconnector.model.analytics;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
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
}

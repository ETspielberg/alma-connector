package org.unidue.ub.libintel.almaconnector.model.analytics;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "row")
public class RequestsItem {

    @JacksonXmlProperty(localName = "MMSId")
    private String mMSId;

    @JacksonXmlProperty(localName = "OwningLibraryCode")
    private String owningLibraryCode;

    @JacksonXmlProperty(localName = "OwningLocationName")
    private String owningLocationName;

    @JacksonXmlProperty(localName = "HoldingId")
    private String holdingId;

    @JacksonXmlProperty(localName = "PermanentCallNumber")
    private String permanentCallNumber;

    @JacksonXmlProperty(localName = "PickupLocation")
    private String pickupLocation;

    @JacksonXmlProperty(localName = "RequestTypeCode")
    private String requestTypeCode;

    @JacksonXmlProperty(localName = "UserGroup")
    private String userGroup;
}

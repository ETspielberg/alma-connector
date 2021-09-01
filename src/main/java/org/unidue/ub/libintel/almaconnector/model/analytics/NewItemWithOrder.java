package org.unidue.ub.libintel.almaconnector.model.analytics;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
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

    @JacksonXmlProperty(localName = "MaterialType")
    private String materialType;
}

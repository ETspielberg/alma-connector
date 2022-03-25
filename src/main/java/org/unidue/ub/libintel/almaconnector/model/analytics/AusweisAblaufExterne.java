package org.unidue.ub.libintel.almaconnector.model.analytics;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "row")
public class AusweisAblaufExterne {

    @JacksonXmlProperty(localName = "PrimaryIdentifier")
    private String identifier;
}

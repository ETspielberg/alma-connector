package org.unidue.ub.libintel.almaconnector.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "row")
public class JobParameter {

    private String name;

    private String value;
}

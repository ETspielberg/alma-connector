package org.unidue.ub.libintel.almaconnector.model.hook;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "event")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "event")
public class HookLoanEvent {

    public static final String JSON_PROPERTY_VALUE = "value";
    @XmlAttribute(name = "value")
    private String value;

    public static final String JSON_PROPERTY_DESC = "desc";
    @XmlElement(name = "desc")
    private String desc;

    @JsonProperty(JSON_PROPERTY_VALUE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @JsonProperty(JSON_PROPERTY_DESC)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "desc")
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

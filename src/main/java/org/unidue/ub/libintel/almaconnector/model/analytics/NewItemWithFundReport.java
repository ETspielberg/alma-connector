package org.unidue.ub.libintel.almaconnector.model.analytics;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "result")
public class NewItemWithFundReport {

    // @JacksonXmlElementWrapper(localName = "rows")
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "row")
    private List<NewItemWithOrderLine> rows;

    public List<NewItemWithOrderLine> getRows() {
        return rows;
    }

    public void setRow(List<NewItemWithOrderLine> rows) {
        this.rows = rows;
    }
}

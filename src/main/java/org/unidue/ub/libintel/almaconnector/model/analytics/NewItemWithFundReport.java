package org.unidue.ub.libintel.almaconnector.model.analytics;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "result")
public class NewItemWithFundReport {

    public final static String PATH = "/shared/Universität Duisburg-Essen 49HBZ_UDE/libintel/NewItemsWithFund";

    // @JacksonXmlElementWrapper(localName = "rows")
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "row")
    private List<NewItemWithOrder> rows;

    public List<NewItemWithOrder> getRows() {
        return rows;
    }

    public void setRow(List<NewItemWithOrder> rows) {
        this.rows = rows;
    }
}

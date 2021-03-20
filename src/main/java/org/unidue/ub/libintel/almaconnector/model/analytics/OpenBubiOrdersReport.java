package org.unidue.ub.libintel.almaconnector.model.analytics;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "result")
public class OpenBubiOrdersReport {

    public final static String PATH = "/shared/Universität Duisburg-Essen 49HBZ_UDE/openBubiOrders";

    // @JacksonXmlElementWrapper(localName = "rows")
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "row")
    private List<OpenBubiOrder> rows;

    public List<OpenBubiOrder> getRows() {
        return rows;
    }

    public void setRow(List<OpenBubiOrder> rows) {
        this.rows = rows;
    }
}

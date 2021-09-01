package org.unidue.ub.libintel.almaconnector.model.analytics;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "result")
public class RequestsReport {

    public final static String PATH = "/shared/Universität Duisburg-Essen 49HBZ_UDE/libintel/Vormerkungen";

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "row")
    List<RequestsItem> rows;

    public List<RequestsItem> getRows() {
        return rows;
    }

    public void setRows(List<RequestsItem> rows) {
        this.rows = rows;
    }
}

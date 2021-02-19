package org.unidue.ub.libintel.almaconnector.model.analytics;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "results")
public class AnalyticsResult<T> {

    @JacksonXmlElementWrapper(localName = "rows")
    @JacksonXmlProperty(localName = "row")
    private List<T> rows;

    public List<T> getRows() {
        return rows;
    }

    public void setRow(List<T> rows) {
        this.rows = rows;
    }
}

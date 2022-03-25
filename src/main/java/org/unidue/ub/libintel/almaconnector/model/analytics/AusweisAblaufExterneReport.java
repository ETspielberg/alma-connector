package org.unidue.ub.libintel.almaconnector.model.analytics;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "result")
public class AusweisAblaufExterneReport {

    public final static String PATH = "/shared/Universität Duisburg-Essen 49HBZ_UDE/libintel/AusweisAblaufExtPromo";

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "row")
    private List<AusweisAblaufExterne> rows;

    public List<AusweisAblaufExterne> getRows() {
        return rows;
    }

    public void setRow(List<AusweisAblaufExterne> rows) {
        this.rows = rows;
    }

    public String retrievePath() {
        return PATH;
    }
}

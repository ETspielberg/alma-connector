package org.unidue.ub.libintel.almaconnector.model.analytics;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "result")
public class OffeneGebuehrenMahnungReport {

    public final static String PATH = "/shared/Universität Duisburg-Essen 49HBZ_UDE/libintel/OffeneGebuehrenMahnung";

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "row")
    private List<OffeneGebuehrenMahnung> rows;

    @JacksonXmlProperty(localName = "isFinished")
    private boolean isFinished;

    @JacksonXmlProperty(localName = "resumptionToken")
    private String resumptionToken;

    public List<OffeneGebuehrenMahnung> getRows() {
        return rows;
    }

    public void setRow(List<OffeneGebuehrenMahnung> rows) {
        this.rows = rows;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public String getResumptionToken() {
        return resumptionToken;
    }

    public void setResumptionToken(String resumptionToken) {
        this.resumptionToken = resumptionToken;
    }

    public String retrievePath() {
        return PATH;
    }
}

package org.unidue.ub.libintel.almaconnector.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "job")
public class JobParametersFile {

    @JacksonXmlElementWrapper(localName = "parameters")
    @JacksonXmlProperty(localName = "parameter")
    private List<JobParameter> parameters;

    public List<JobParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<JobParameter> rows) {
        this.parameters = rows;
    }

    public JobParametersFile() {
        this.parameters = new ArrayList<>();
    }
}

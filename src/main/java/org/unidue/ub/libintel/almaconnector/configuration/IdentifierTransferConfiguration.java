package org.unidue.ub.libintel.almaconnector.configuration;

public class IdentifierTransferConfiguration {

    private final static String ALMA_ANALYTICS_BASE_URL = "/shared/Universität Duisburg-Essen 49HBZ_UDE/libintel/";

    private String setId;

    private String jobId;

    private String name;

    public Class<?> getReportClass() throws ClassNotFoundException {
        return Class.forName("org.unidue.ub.libintel.almaconnector.model.analytics." + name + "Report");
    }


    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getPath() {
        return ALMA_ANALYTICS_BASE_URL + name;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {return String.format("name: %s, setId: %s, jobId: %s", name, setId, jobId);}
}

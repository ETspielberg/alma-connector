package org.unidue.ub.libintel.almaconnector.model.hook;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.unidue.ub.alma.shared.conf.GeneralInstitution;
import org.unidue.ub.alma.shared.conf.JobInstance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement(name = "loan_hook")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "loan_hook")
public class JobHook {

    public static final String JSON_PROPERTY_ID = "id";
    @XmlElement(name = "id")
    private String id;

    public static final String JSON_PROPERTY_ACTION = "action";
    @XmlElement(name = "action")
    private String action;

    public static final String JSON_PROPERTY_TIME = "time";
    @XmlElement(name = "time")
    private Date time;

    public static final String JSON_PROPERTY_INSTITUTION = "institution";
    @XmlElement(name = "institution")
    private GeneralInstitution institution;

    public static final String JSON_PROPERTY_EVENT = "event";
    @XmlElement(name = "event")
    private HookLoanEvent event;

    public static final String JSON_PROPERTY_ITEM_LOAN = "job_instance";
    @XmlElement(name = "job_instance")
    private JobInstance jobInstance;

    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty(JSON_PROPERTY_ACTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "action")
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @JsonProperty(JSON_PROPERTY_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "time")
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @JsonProperty(JSON_PROPERTY_ITEM_LOAN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "job_instance")
    public JobInstance getJobInstance() {
        return jobInstance;
    }

    public void setJobInstance(JobInstance jobInstance) {
        this.jobInstance = jobInstance;
    }

    @JsonProperty(JSON_PROPERTY_INSTITUTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "institution")
    public GeneralInstitution getInstitution() {
        return institution;
    }

    public void setInstitution(GeneralInstitution institution) {
        this.institution = institution;
    }

    @JsonProperty(JSON_PROPERTY_EVENT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "event")
    public HookLoanEvent getEvent() {
        return event;
    }

    public void setEvent(HookLoanEvent event) {
        this.event = event;
    }
}

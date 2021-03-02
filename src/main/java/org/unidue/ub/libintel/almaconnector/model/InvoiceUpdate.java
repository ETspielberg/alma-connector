package org.unidue.ub.libintel.almaconnector.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.unidue.ub.alma.shared.acq.Payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * Invoice update object.
 */
@XmlRootElement(name = "invoice")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "invoice")
public class InvoiceUpdate {

    private static final String JSON_PROPERTY_PAYMENT = "payment";
    @XmlElement(name = "payment")
    private Payment payment;

    public InvoiceUpdate(Payment payment) {
        this.payment = payment;
    }

    /**
     * Get payment
     * @return payment
     **/
    @JsonProperty(JSON_PROPERTY_PAYMENT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "payment")
    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}

package org.unidue.ub.libintel.almaconnector.model.analytics;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@JacksonXmlRootElement(localName = "row")
public class GebuehrenSichernTaeglich {

    @JacksonXmlProperty(localName = "Column0")
    private String dummyField;

    @JacksonXmlProperty(localName = "Column2")
    private String fineFeeId;

    @JacksonXmlProperty(localName = "Column1")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate creationDate;

    @JacksonXmlProperty(localName = "Column3")
    private String fineFeeStatus;

    @JacksonXmlProperty(localName = "Column5")
    private String fineFeeType;

    @JacksonXmlProperty(localName = "Column12")
    private Double originalAmount;

    @JacksonXmlProperty(localName = "Column4")
    private String fineFeeTransactionId;

    @JacksonXmlProperty(localName = "Column6")
    private String fineFeeTransactionType;

    @JacksonXmlProperty(localName = "Column8")
    private LocalDate transactionDate;

    @JacksonXmlProperty(localName = "Column7")
    private String paymentMethod;

    @JacksonXmlProperty(localName = "Column13")
    private Double transactionAmount;

    @JacksonXmlProperty(localName = "Column11")
    private String primaryIdentifier;

    @JacksonXmlProperty(localName = "Column9")
    private String firstName;

    @JacksonXmlProperty(localName = "Column10")
    private String lastName;

    public String toString() {
        return String.format("fine fee id: %s, fine fee status: %s, fine fee type: %s, original amount: %s, primary identifier: %s, name: %s %s", fineFeeId, fineFeeStatus, fineFeeType, originalAmount, primaryIdentifier, firstName, lastName);
    }
}

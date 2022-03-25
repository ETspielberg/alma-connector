package org.unidue.ub.libintel.almaconnector.model.analytics;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class ApcData {

    @JacksonXmlProperty(localName = "Column0")
    private String dummyField;

    @JacksonXmlProperty(localName = "Column1")
    private String mmsId;

    @JacksonXmlProperty(localName = "Column2")
    private String ledger = "";

    @JacksonXmlProperty(localName = "Column3")
    private String ledgerType = "";

    @JacksonXmlProperty(localName = "Column4")
    private String explicitRatio;

    @JacksonXmlProperty(localName = "Column5")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate invoiceCreationDate;

    @JacksonXmlProperty(localName = "Column6")
    private String currency = "";

    @JacksonXmlProperty(localName = "Column7")
    private String invoiceLineNote = "";

    @JacksonXmlProperty(localName = "Column8")
    private String invoiceLinePriceNote = "";

    @JacksonXmlProperty(localName = "Column9")
    private double vatPercent = 0.0;

    @JacksonXmlProperty(localName = "Column10")
    private String vatCode = "";

    @JacksonXmlProperty(localName = "Column11")
    private double vatNoteData = 0.0;

    @JacksonXmlProperty(localName = "Column12")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate invoiceDate;

    @JacksonXmlProperty(localName = "Column13")
    private String invoiceNumber = "";

    @JacksonXmlProperty(localName = "Column14")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate voucherDate;

    @JacksonXmlProperty(localName = "Column15")
    private String voucherNumber = "";

    @JacksonXmlProperty(localName = "Column16")
    private double vatAmount = 0.0;

    @JacksonXmlProperty(localName = "Column17")
    private String vatType = "";

    @JacksonXmlProperty(localName = "Column18")
    private double listPrice = 0.0;

    @JacksonXmlProperty(localName = "Column19")
    private String orderlineNumber  = "";

    @JacksonXmlProperty(localName = "Column20")
    private String creditor = "";

    @JacksonXmlProperty(localName = "Column21")
    private String vendorCode = "";

    @JacksonXmlProperty(localName = "Column22")
    private String vendorName = "";

    @JacksonXmlProperty(localName = "Column23")
    private double totalPrice = 0.0;

    @JacksonXmlProperty(localName = "Column24")
    private double transactionAmount = 0.0;
}

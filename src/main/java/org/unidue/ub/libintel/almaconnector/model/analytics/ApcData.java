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
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate invoiceCreationDate;

    @JacksonXmlProperty(localName = "Column5")
    private String currency = "";

    @JacksonXmlProperty(localName = "Column6")
    private String invoiceLineNote = "";

    @JacksonXmlProperty(localName = "Column7")
    private String invoiceLinePriceNote = "";

    @JacksonXmlProperty(localName = "Column8")
    private double vatPercent = 0.0;

    @JacksonXmlProperty(localName = "Column9")
    private String vatCode = "";

    @JacksonXmlProperty(localName = "Column10")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate invoiceDate;

    @JacksonXmlProperty(localName = "Column11")
    private String invoiceNumber = "";

    @JacksonXmlProperty(localName = "Column12")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate voucherDate;

    @JacksonXmlProperty(localName = "Column13")
    private String voucherNumber = "";

    @JacksonXmlProperty(localName = "Column14")
    private double vatAmount = 0.0;

    @JacksonXmlProperty(localName = "Column15")
    private double listPrice = 0.0;

    @JacksonXmlProperty(localName = "Column16")
    private String orderlineNumber  = "";

    @JacksonXmlProperty(localName = "Column17")
    private String creditor = "";

    @JacksonXmlProperty(localName = "Column18")
    private String vendorCode = "";

    @JacksonXmlProperty(localName = "Column19")
    private String vendorName = "";

    @JacksonXmlProperty(localName = "Column20")
    private double totalPrice = 0.0;

    @JacksonXmlProperty(localName = "Column21")
    private double transactionAmount = 0.0;
}

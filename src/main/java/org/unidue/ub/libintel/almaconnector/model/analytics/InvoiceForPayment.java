package org.unidue.ub.libintel.almaconnector.model.analytics;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;


@JacksonXmlRootElement(localName = "row")
@Data
public class InvoiceForPayment {

    @JacksonXmlProperty(localName = "InvoiceLine-Status")
    private String invoiceLineStatus;

    @JacksonXmlProperty(localName = "Invoice-Number")
    private String invoiceNumber;

    @JacksonXmlProperty(localName = "Invoice-Status")
    private String invoiceStatus;

    @JacksonXmlProperty(localName = "OrderLineType")
    private String orderLineType;

    @JacksonXmlProperty(localName = "PONumber")
    private String poNumber;

    @JacksonXmlProperty(localName = "Invoice-OwnerCode")
    private String invoiceOwnerCode;

    @JacksonXmlProperty(localName = "ERPCode")
    private String erpCode;
}

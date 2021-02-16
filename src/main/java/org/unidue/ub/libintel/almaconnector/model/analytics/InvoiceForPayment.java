package org.unidue.ub.libintel.almaconnector.model.analytics;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@JacksonXmlRootElement(localName = "row")
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

    public String getInvoiceLineStatus() {
        return invoiceLineStatus;
    }

    public void setInvoiceLineStatus(String invoiceLineStatus) {
        this.invoiceLineStatus = invoiceLineStatus;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(String invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public String getOrderLineType() {
        return orderLineType;
    }

    public void setOrderLineType(String orderLineType) {
        this.orderLineType = orderLineType;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getInvoiceOwnerCode() {
        return invoiceOwnerCode;
    }

    public void setInvoiceOwnerCode(String invoiceOwnerCode) {
        this.invoiceOwnerCode = invoiceOwnerCode;
    }

    public String getErpCode() {
        return erpCode;
    }

    public void setErpCode(String erpCode) {
        this.erpCode = erpCode;
    }
}

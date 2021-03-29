package org.unidue.ub.libintel.almaconnector.model.analytics;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "result")
public class InvoiceForPaymentReport {

    public final static String PATH = "/shared/Universität Duisburg-Essen 49HBZ_UDE/Rechnungen zur Bezahlung";

    // @JacksonXmlElementWrapper(localName = "rows")
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "row")
    private List<InvoiceForPayment> rows;

    public List<InvoiceForPayment> getRows() {
        return rows;
    }

    public void setRow(List<InvoiceForPayment> rows) {
        this.rows = rows;
    }
}
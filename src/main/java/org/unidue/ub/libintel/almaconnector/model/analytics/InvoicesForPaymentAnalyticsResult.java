package org.unidue.ub.libintel.almaconnector.model.analytics;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "results")
public class InvoicesForPaymentAnalyticsResult {

    @JacksonXmlElementWrapper(localName = "rows")
    @JacksonXmlProperty(localName = "row")
    private List<InvoiceForPayment> rows;

    public List<InvoiceForPayment> getRows() {
        return rows;
    }

    public void setRow(List<InvoiceForPayment> rows) {
        this.rows = rows;
    }
}

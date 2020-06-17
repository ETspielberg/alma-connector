package org.unidue.ub.libintel.almaconnector.model;

import java.util.Date;

/**
 *
 */
public class SapResponse {

    private String runId;

    private String creditor;

    private String invoiceNumber;

    private double amount;

    private String currency;

    private String voucherNumber;

    private Date invoiceFrom;

    private Date invoiceTo;

    public SapResponse(String runId) {
        this.runId = runId;
    }

    public SapResponse(String runId, String creditor, String invoiceNumber, double amount, String currency, String voucherNumber) {
        this.runId = runId;
        this.creditor = creditor;
        this.invoiceNumber = invoiceNumber;
        this.amount = amount;
        this.voucherNumber = voucherNumber;
        this.currency = currency;
    }

    public SapResponse withCreditor(String creditor) {
        this.creditor = creditor;
        return this;
    }

    public SapResponse withCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public SapResponse withInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
        return this;
    }

    public SapResponse withAmount(double amount) {
        this.amount = amount;
        return this;
    }

   public SapResponse withVoucherNumber(String voucherNumber) {
        this.voucherNumber = voucherNumber;
        return this;
   }

   public SapResponse withInvoiceFrom(Date invoiceFrom) {
        this.invoiceFrom = invoiceFrom;
        return this;
   }

   public SapResponse withInvoiceTo(Date invoiceTo) {
        this.invoiceTo = invoiceTo;
        return this;
   }

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getCreditor() {
        return creditor;
    }

    public void setCreditor(String creditor) {
        this.creditor = creditor;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getVoucherNumber() {
        return voucherNumber;
    }

    public void setVoucherNumber(String voucherNumber) {
        this.voucherNumber = voucherNumber;
    }

    public Date getInvoiceFrom() {
        return invoiceFrom;
    }

    public void setInvoiceFrom(Date invoiceFrom) {
        this.invoiceFrom = invoiceFrom;
    }

    public Date getInvoiceTo() {
        return invoiceTo;
    }

    public void setInvoiceTo(Date invoiceTo) {
        this.invoiceTo = invoiceTo;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}

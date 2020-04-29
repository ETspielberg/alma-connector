package org.unidue.ub.libintel.almaconnector.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SapData {

    private final static String csvLine = "%s; %s; %s; %s; %s; %f; %s; %s; %s; %s; %s; %s;";

    private final static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public String vendorCode;

    public String creditor;

    public Date commitmentDate;

    public Date invoiceDate;

    public String costType;

    public double invoiceAmount;

    public String invoiceNumber;

    public String currency;

    public String positionalNumber;

    public String ledgerAccount;

    public Date fromDate;

    public Date toDate;

    public SapData() {
    }

    public SapData withVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
        return this;
    }

    public SapData withCreditor(String creditor) {
        this.creditor = creditor;
        return this;
    }

    public SapData withCommitmentDate(Date commitmentDate) {
        this.commitmentDate = commitmentDate;
        return this;
    }

    public SapData withInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
        return this;
    }

    public SapData withCostType(String costType) {
        this.costType = costType;
        return this;
    }

    public SapData withInvoiceAmount(double invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
        return this;
    }

    public SapData withInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
        return this;
    }

    public SapData withPositionalNumber(String positionalNumber) {
        this.positionalNumber = positionalNumber;
        return this;
    }

    public SapData withLedgerAccount(String ledgerAccount) {
        this.ledgerAccount = ledgerAccount;
        return this;
    }

    public SapData withCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public SapData withFromDate(Date fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    public SapData withToDate(Date toDate) {
        this.toDate = toDate;
        return this;
    }

    public String toCsv() {
        return String.format(csvLine,
                this.vendorCode,
                this.creditor,
                formatter.format(this.commitmentDate),
                formatter.format(this.invoiceDate),
                this.costType,
                this.currency,
                this.invoiceAmount,
                this.invoiceNumber,
                this.positionalNumber,
                this.ledgerAccount,
                getDateString(this.fromDate),
                getDateString(this.toDate));
    }

    public String toFixedLengthLine() {
        String string = "";
        string += getSizedString(this.vendorCode, 10);
        string += getSizedString(formatter.format(this.commitmentDate), 12);
        string += getSizedString(formatter.format(this.invoiceDate), 12);
        string += getSizedString(this.costType, 2);
        string += getSizedString(String.valueOf(this.invoiceAmount), 7);
        string += getSizedString(this.invoiceNumber, 16);
        string += getSizedString(this.currency, 4);
        string += getSizedString(this.positionalNumber, 6);
        string += getSizedString(this.ledgerAccount, 16);
        if (this.fromDate != null)
            string += getSizedString(formatter.format(this.fromDate), 12);
        else
            string += getSizedString("", 12);
        if (this.toDate != null)
            string += getSizedString(formatter.format(this.toDate), 12);
        else
            string += getSizedString("", 12);
        return string;
    }

    private static String getSizedString(String string, int length) {
        return String.format("%1$" + length + "s", string);
    }

    private static String getDateString(Date date) {
        if (date == null)
            return "";
        else
            return formatter.format(date);
    }

}

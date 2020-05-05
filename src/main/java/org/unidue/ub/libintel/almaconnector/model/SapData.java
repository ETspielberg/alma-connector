package org.unidue.ub.libintel.almaconnector.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SapData {

    private final static SimpleDateFormat readableDateFormatter = new SimpleDateFormat("dd.MM.yyyy");

    private final static SimpleDateFormat sapDateFormatter = new SimpleDateFormat("yyyyMMdd");

    public String vendorCode;

    public String creditor;

    public Date commitmentDate;

    public Date invoiceDate;

    public String costType;

    public double invoiceAmount;

    public String invoiceNumber;

    public String currency;

    public String positionalNumber;

    public SapAccountData sapAccountData;

    public Date fromDate;

    public Date toDate;

    public String comment;

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

    public SapData withSapAccountData(SapAccountData sapAccountData) {
        this.sapAccountData = sapAccountData;
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

    public SapData withComment(String comment) {
        this.comment = comment;
        return this;
    }

    public String toCsv() {
        String string = this.vendorCode + ";";
        string += this.creditor + ";";
        string += sapDateFormatter.format(this.commitmentDate) + ";";
        string += this.currency + ";";
        string += this.invoiceAmount + ";";
        string += getSizedString(this.positionalNumber, 5).replace(" ", "0") + ";";
        string += this.comment + ";";
        if (this.creditor == null || this.creditor.isEmpty()) {
            string += "C;";
        } else {
            string += "K;";
        }
        string += this.sapAccountData.getLedgerAccount() + this.sapAccountData.getFonds() + ";";
        if (!this.sapAccountData.getPspElement().isEmpty()) {
            string += "P;";
            string += this.sapAccountData.getPspElement() + ";";
        } else {
            string += "K;";
            string += this.sapAccountData.getCostCentre() + ";";
        }
        return string;
    }

    public String toFixedLengthLine() {
        String string = "";
        string += getSizedString(this.vendorCode, 7);
        if (this.creditor == null)
            string += getSizedString("", 10);
        else
            string += getSizedString(this.creditor, 10);
        string += getSizedString(readableDateFormatter.format(this.commitmentDate), 12);
        string += getSizedString(readableDateFormatter.format(this.invoiceDate), 12);
        if (this.creditor == null || this.creditor.isEmpty())
            string += getSizedString("C", 4);
        else
            string += getSizedString("K", 4);
        string += getSizedString(String.valueOf(this.invoiceAmount), 14);
        string += getSizedString(this.currency, 8);
        string += getSizedString(this.invoiceNumber, 22);
        string += getSizedString(getSizedString(this.positionalNumber, 5).replace(" ", "0"), 7);
        string += getSizedString(this.costType, 12);
        string += getSizedString(this.sapAccountData.getImportCheckString(), 30);
        if (this.fromDate != null)
            string += getSizedString(readableDateFormatter.format(this.fromDate), 12);
        else
            string += getSizedString("0", 12);
        if (this.toDate != null)
            string += getSizedString(readableDateFormatter.format(this.toDate), 12);
        else
            string += getSizedString("0", 12);
        return string;
    }

    private static String getSizedString(String string, int length) {
        return String.format("%1$" + length + "s", string);
    }
}

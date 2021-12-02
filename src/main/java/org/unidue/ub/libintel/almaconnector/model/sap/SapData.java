package org.unidue.ub.libintel.almaconnector.model.sap;

import org.springframework.format.annotation.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * holds all Data for a single invoice line to be uploaded to SAP
 */
public class SapData implements Comparable<SapData> {

    private final static SimpleDateFormat readableDateFormatter = new SimpleDateFormat("dd.MM.yyyy");

    private final static SimpleDateFormat sapDateFormatter = new SimpleDateFormat("yyyyMMdd");

    public String vendorCode;

    public String creditor;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    public Date commitmentDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    public Date invoiceDate;

    public String costType;

    public double invoiceAmount;

    public String invoiceNumber;

    public String currency;

    public String positionalNumber;

    public SapAccountData sapAccountData;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    public Date fromDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    public Date toDate;

    public String comment;

    public boolean isChecked = false;

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

    public boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean checked) {
        isChecked = checked;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public String getCreditor() {
        return creditor;
    }

    public void setCreditor(String creditor) {
        this.creditor = creditor;
    }

    public Date getCommitmentDate() {
        return commitmentDate;
    }

    public void setCommitmentDate(Date commitmentDate) {
        this.commitmentDate = commitmentDate;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getCostType() {
        return costType;
    }

    public void setCostType(String costType) {
        this.costType = costType;
    }

    public double getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(double invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPositionalNumber() {
        return positionalNumber;
    }

    public void setPositionalNumber(String positionalNumber) {
        this.positionalNumber = positionalNumber;
    }

    public SapAccountData getSapAccountData() {
        return sapAccountData;
    }

    public void setSapAccountData(SapAccountData sapAccountData) {
        this.sapAccountData = sapAccountData;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String toCsv() {
        String string = this.vendorCode + ";";
        if (this.creditor != null)
            string += this.creditor + ";";
        else
            string += ";";
        string += sapDateFormatter.format(this.commitmentDate) + ";";
        string += sapDateFormatter.format(this.invoiceDate) + ";";
        if (this.costType != null)
            string += this.costType + ";";
        else
            string += ";";
        string += this.currency + ";";
        string += String.format("%1$,.2f", this.invoiceAmount) + ";";
        string += this.invoiceNumber + ";";
        string += getSizedString(this.positionalNumber, 5).replace(" ", "0") + ";";
        string += this.comment + ";";
        if (this.fromDate != null)
            string += sapDateFormatter.format(this.fromDate) + ";";
        else
            string += "0;";
        if (this.toDate != null)
            string += sapDateFormatter.format(this.toDate) + ";";
        else
            string += "0;";
        string += getCheckCharacter() + ";";
        string += this.sapAccountData.getSapString();
        return string;
    }

    public String getCheckCharacter() {
        if (this.vendorCode != null) {
            if (this.vendorCode.equals("A-500") || this.vendorCode.equals("A-510") || this.vendorCode.equals("A-520"))
                return"C";
            else
                return "K";
        } else
            return "K";
    }

    public String toFixedLengthLine() {
        String string = "";
        string += getSizedString(this.vendorCode, 14);
        if (this.creditor == null)
            string += getSizedString("", 14);
        else
            string += getSizedString(this.creditor, 14);
        string += getSizedString(readableDateFormatter.format(this.commitmentDate), 12);
        string += getSizedString(readableDateFormatter.format(this.invoiceDate), 12);
        string += getSizedString(this.costType, 12);
        string += getSizedString(String.format("%1$,.2f", this.invoiceAmount), 14);
        string += getSizedString(this.currency, 8);
        string += getSizedString(this.invoiceNumber, 22);
        string += getSizedString(getSizedString(this.positionalNumber, 5).replace(" ", "0"), 7);
        string += getSizedString(getCheckCharacter(), 4);
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

    @Override
    public int compareTo(SapData other) {
        if (this.creditor == null || this.creditor.isEmpty()) {
            if (other.creditor == null || other.creditor.isEmpty())
                return compareVendorCodes(other);
            else
                return 10;
        }
        else {
            if (other.creditor == null || other.creditor.isEmpty()) {
                return -10;
            } else {
                return compareVendorCodes(other);
            }
        }
    }

    private int compareVendorCodes(SapData other) {
        if (this.vendorCode == null || this.vendorCode.isEmpty()) {
            if (other.vendorCode == null || other.vendorCode.isEmpty()) {
                return compareInvoiceNumber(other);
            } else {
                return 1;
            }
        } else {
            if (other.vendorCode == null || other.vendorCode.isEmpty()) {
                return -1;
            } else {
                if (this.vendorCode.equals(other.vendorCode))
                        return this.compareInvoiceNumber(other);
                else
                    return this.vendorCode.compareTo(other.vendorCode);
            }
        }
    }

    private int compareInvoiceNumber(SapData other) {
        if (this.invoiceNumber == null || this.invoiceNumber.isEmpty()) {
            if (other.invoiceNumber == null || other.invoiceNumber.isEmpty()) {
                return 0;
            } else {
                return 1;
            }
        } else {
            if (other.invoiceNumber == null || other.invoiceNumber.isEmpty()) {
                return -1;
            } else {
                return this.invoiceNumber.compareTo(other.invoiceNumber);
            }
        }
    }
}

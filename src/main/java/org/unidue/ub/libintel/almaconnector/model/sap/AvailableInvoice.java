package org.unidue.ub.libintel.almaconnector.model.sap;

import lombok.Data;
import org.unidue.ub.alma.shared.acq.Invoice;

import java.util.Date;

@Data
public class AvailableInvoice {

    private String invoiceNumber;

    private String vendorAccount;

    public Date invoiceDate;

    private String owner;

    private String currency;

    private double amount;

    private boolean isChecked;

    public AvailableInvoice() {
        this.isChecked = false;
    }

    public AvailableInvoice(Invoice invoice) {
        this.isChecked = false;
        this.invoiceNumber = invoice.getNumber();
        this.vendorAccount = invoice.getVendor().getValue();
        this.owner = invoice.getOwner().getValue();
        this.currency = invoice.getCurrency().getValue();
        this.amount = invoice.getTotalAmount();
        this.invoiceDate = invoice.getInvoiceDate();
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getVendorAccount() {
        return vendorAccount;
    }

    public void setVendorAccount(String vendorAccount) {
        this.vendorAccount = vendorAccount;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean checked) {
        isChecked = checked;
    }
}

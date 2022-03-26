package org.unidue.ub.libintel.almaconnector.model.datastore;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="user_with_fees")
public class UserWithFees {

    @Id
    @Column(name = "cash_transaction_id")
    private String cashTransactionId;

    @Column(name = "cash_transaction_rec_key")
    private String cashTransactionRecKey;

    @Column(name = "cash_transaction_date")
    private String cashTransactionDate;

    @Column(name = "cash_transaction_status")
    private String cashTransactionStatus;

    @Column(name = "cash_transaction_type")
    private String cashTransactionType;

    @Column(name = "cash_transaction_credit_debit")
    private String cashTransactionCreditDebit;

    @Column(name = "cash_transaction_sum")
    private String cashTransactionSum;

    @Column(name = "cash_transaction_payment_date")
    private Date cashTransactionPaymentDate;

    @Column(name = "cash_transaction_payment_ip")
    private String cashTransactionPaymentIP;

    @Column(name = "cash_transaction_payment_mode")
    private String cashTransactionPaymentMode;

    @Column(name = "cash_transaction_payment_timestamp")
    private String cashTransactionPaymentTimestamp;

    @Column(name = "cash_patron_name")
    private String cashPatronName;

    @Column(name = "patron_id")
    private String patronId;

    @Column(name = "saved_on")
    private Date savedOn;

    public UserWithFees() {this.savedOn = new Date(); }

    public String getCashTransactionId() {
        return cashTransactionId;
    }

    public void setCashTransactionId(String cashTransactionId) {
        this.cashTransactionId = cashTransactionId;
    }

    public String getCashTransactionRecKey() {
        return cashTransactionRecKey;
    }

    public void setCashTransactionRecKey(String cashTransactionRecKey) {
        this.cashTransactionRecKey = cashTransactionRecKey;
    }

    public String getCashTransactionDate() {
        return cashTransactionDate;
    }

    public void setCashTransactionDate(String cashTransactionDate) {
        this.cashTransactionDate = cashTransactionDate;
    }

    public String getCashTransactionStatus() {
        return cashTransactionStatus;
    }

    public void setCashTransactionStatus(String cashTransactionStatus) {
        this.cashTransactionStatus = cashTransactionStatus;
    }

    public String getCashTransactionType() {
        return cashTransactionType;
    }

    public void setCashTransactionType(String cashTransactionType) {
        this.cashTransactionType = cashTransactionType;
    }

    public String getCashTransactionCreditDebit() {
        return cashTransactionCreditDebit;
    }

    public void setCashTransactionCreditDebit(String cashTransactionCreditDebit) {
        this.cashTransactionCreditDebit = cashTransactionCreditDebit;
    }

    public String getCashTransactionSum() {
        return cashTransactionSum;
    }

    public void setCashTransactionSum(String cashTransactionSum) {
        this.cashTransactionSum = cashTransactionSum;
    }

    public Date getCashTransactionPaymentDate() {
        return cashTransactionPaymentDate;
    }

    public void setCashTransactionPaymentDate(Date cashTransactionPaymentDate) {
        this.cashTransactionPaymentDate = cashTransactionPaymentDate;
    }

    public String getCashTransactionPaymentIP() {
        return cashTransactionPaymentIP;
    }

    public void setCashTransactionPaymentIP(String cashTransactionPaymentIP) {
        this.cashTransactionPaymentIP = cashTransactionPaymentIP;
    }

    public String getCashTransactionPaymentMode() {
        return cashTransactionPaymentMode;
    }

    public void setCashTransactionPaymentMode(String cashTransactionPaymentMode) {
        this.cashTransactionPaymentMode = cashTransactionPaymentMode;
    }

    public String getCashTransactionPaymentTimestamp() {
        return cashTransactionPaymentTimestamp;
    }

    public void setCashTransactionPaymentTimestamp(String cashTransactionPaymentTimestamp) {
        this.cashTransactionPaymentTimestamp = cashTransactionPaymentTimestamp;
    }

    public String getCashPatronName() {
        return cashPatronName;
    }

    public void setCashPatronName(String cashPatronName) {
        this.cashPatronName = cashPatronName;
    }

    public String getPatronId() {
        return patronId;
    }

    public void setPatronId(String patronId) {
        this.patronId = patronId;
    }

    public Date getSavedOn() {
        return savedOn;
    }

    public void setSavedOn(Date savedOn) {
        this.savedOn = savedOn;
    }
}

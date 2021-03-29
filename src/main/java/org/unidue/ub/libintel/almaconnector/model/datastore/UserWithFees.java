package org.unidue.ub.libintel.almaconnector.model.datastore;

import java.util.Date;

public class UserWithFees {

    private String cashTransactionId;

    private String cashTransactionRecKey;

    private String cashTransactionDate;

    private String cashTransactionStatus;

    private String cashTransactionType;

    private String cashTransactionCreditDebit;

    private String cashTransactionSum;

    private Date cashTransactionPaymentDate;

    private String cashTransactionPaymentIP;

    private String cashTransactionPaymentMode;

    private String cashTransactionPaymentTimeStamp;

    private String cashPatronName;

    private String patronId;

    private Date savedOn;

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

    public String getCashTransactionPaymentTimeStamp() {
        return cashTransactionPaymentTimeStamp;
    }

    public void setCashTransactionPaymentTimeStamp(String cashTransactionPaymentTimeStamp) {
        this.cashTransactionPaymentTimeStamp = cashTransactionPaymentTimeStamp;
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

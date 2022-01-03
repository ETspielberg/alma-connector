package org.unidue.ub.libintel.almaconnector.model.jobs;

import org.unidue.ub.libintel.almaconnector.model.analytics.GebuehrenSichernTaeglich;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "user_fine_fee")
public class UserFineFee {

    @Id
    @Column(name = "fine_fee_id")
    private String fineFeeId;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "fine_fee_status")
    private String fineFeeStatus;

    @Column(name = "fine_fee_type")
    private String fineFeeType;

    @Column(name = "original_amount")
    private Double originalAmount;

    @Column(name = "fine_fee_transaction_id")
    private String fineFeeTransactionId;

    @Column(name = "fine_fee_transaction_type")
    private String fineFeeTransactionType;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "transaction_amount")
    private Double transactionAmount;

    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @Column(name = "primary_identifier")
    private String primaryIdentifier;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    public UserFineFee() {}

    public UserFineFee(GebuehrenSichernTaeglich gebuehren) {
        this.fineFeeId = gebuehren.getFineFeeId();
        this.creationDate = gebuehren.getCreationDate();
        this.fineFeeStatus = gebuehren.getFineFeeStatus();
        this.fineFeeType = gebuehren.getFineFeeType();
        this.originalAmount = gebuehren.getOriginalAmount();
        this.fineFeeTransactionId = gebuehren.getFineFeeTransactionId();
        this.fineFeeTransactionType = gebuehren.getFineFeeTransactionType();
        this.transactionDate = gebuehren.getTransactionDate();
        this.paymentMethod = gebuehren.getPaymentMethod();
        this.transactionAmount = gebuehren.getTransactionAmount();
        this.primaryIdentifier = gebuehren.getPrimaryIdentifier();
        this.firstName = gebuehren.getFirstName();
        this.lastName = gebuehren.getLastName();
    }

    public String getFineFeeId() {
        return fineFeeId;
    }

    public void setFineFeeId(String fineFeeId) {
        this.fineFeeId = fineFeeId;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public String getFineFeeStatus() {
        return fineFeeStatus;
    }

    public void setFineFeeStatus(String fineFeeStatus) {
        this.fineFeeStatus = fineFeeStatus;
    }

    public String getFineFeeType() {
        return fineFeeType;
    }

    public void setFineFeeType(String fineFeeType) {
        this.fineFeeType = fineFeeType;
    }

    public Double getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(Double originalAmount) {
        this.originalAmount = originalAmount;
    }

    public String getFineFeeTransactionId() {
        return fineFeeTransactionId;
    }

    public void setFineFeeTransactionId(String fineFeeTransactionId) {
        this.fineFeeTransactionId = fineFeeTransactionId;
    }

    public String getFineFeeTransactionType() {
        return fineFeeTransactionType;
    }

    public void setFineFeeTransactionType(String fineFeeTransactionType) {
        this.fineFeeTransactionType = fineFeeTransactionType;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getPrimaryIdentifier() {
        return primaryIdentifier;
    }

    public void setPrimaryIdentifier(String primaryIdentifier) {
        this.primaryIdentifier = primaryIdentifier;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public UserFineFee update(GebuehrenSichernTaeglich gebuehrenSichernTaeglich) {
        this.fineFeeStatus = gebuehrenSichernTaeglich.getFineFeeStatus();
        this.fineFeeTransactionId = gebuehrenSichernTaeglich.getFineFeeTransactionId();
        this.fineFeeTransactionType = gebuehrenSichernTaeglich.getFineFeeTransactionType();
        this.paymentMethod = gebuehrenSichernTaeglich.getPaymentMethod();
        this.transactionAmount = gebuehrenSichernTaeglich.getTransactionAmount();
        this.transactionDate = gebuehrenSichernTaeglich.getTransactionDate();
        return this;
    }
}

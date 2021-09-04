package org.unidue.ub.libintel.almaconnector.model.datastore;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name="user_with_fees")
public class UserWithFees {

    @Id
    @Column(name = "cash_transaction_id")
    private String cashTransactionId;

    @Column(name = "cash_transaction_id")
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

    @Column(name = "cash_transaction_üayment_ip")
    private String cashTransactionPaymentIP;

    @Column(name = "cash_transaction_payment_mode")
    private String cashTransactionPaymentMode;

    @Column(name = "cash_transaction_payment_time_stamp")
    private String cashTransactionPaymentTimeStamp;

    @Column(name = "cash_patron_name")
    private String cashPatronName;

    @Column(name = "patron_id")
    private String patronId;

    @Column(name = "saved_on")
    private Date savedOn;

    public UserWithFees() {this.savedOn = new Date(); }
}

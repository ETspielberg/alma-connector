package org.unidue.ub.libintel.almaconnector.model.datastore;

import lombok.Data;

import java.util.Date;

@Data
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
}

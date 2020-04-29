package org.unidue.ub.libintel.almaconnector;

import org.unidue.ub.alma.shared.acq.FundDistribution;
import org.unidue.ub.alma.shared.acq.Invoice;
import org.unidue.ub.alma.shared.acq.InvoiceLine;
import org.unidue.ub.alma.shared.acq.Vendor;
import org.unidue.ub.libintel.almaconnector.model.SapData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class utils {

    public static List<SapData> convertInvoiceToSapData(Invoice invoice, Vendor vendor) {
        List<SapData> sapDataList = new ArrayList<>();
        if (invoice.getInvoiceLines() != null) {
            for (InvoiceLine invoiceLine : invoice.getInvoiceLines().getInvoiceLine()) {
                for (FundDistribution fundDistribution : invoiceLine.getFundDistribution()) {
                    SapData sapData = new SapData()
                            .withCurrency(invoice.getCurrency().getValue())
                            .withInvoiceAmount(fundDistribution.getAmount())
                            .withInvoiceDate(invoice.getInvoiceDate())
                            .withCostType("T")
                            .withVendorCode(invoice.getVendor().getValue())
                            .withCreditor(vendor.getAdditionalCode())
                            .withToDate(invoiceLine.getSubscriptionToDate())
                            .withFromDate(invoiceLine.getSubscriptionFromDate())
                            .withCommitmentDate(new Date())
                            .withCurrency(invoice.getCurrency().getValue())
                            .withPositionalNumber(invoiceLine.getNumber())
                            .withLedgerAccount(fundDistribution.getFundCode().getValue())
                            .withInvoiceNumber(invoice.getNumber());
                    sapDataList.add(sapData);
                }
            }
        }
        return sapDataList;
    }

    private final String calulateLedgerAccount(String fundCode) {
        String ledgerAccount = "6810";
        String fonds = "0000";
        String pspElement = "";

        String[] parts = fundCode.split("-");
        if (parts[0].startsWith("S")) {
            pspElement = parts[1];
            fonds = parts[2];
        }
        else if (parts[1].startsWith("1")) {
            fonds = parts[1];
            ledgerAccount = "6810" + parts[2];
        } else if (parts[1].startsWith("0")) {
            fonds = "0000";
            ledgerAccount = "6810" + parts[1];
            pspElement = "555100000" + "9" + parts[0];
        } else if(parts[1].startsWith("5")) {
            fonds = "1400";
            ledgerAccount = "6810" + parts[1];
            pspElement = "555100000" + "9" + parts[0];
        }
        if (pspElement.length() == 14)
            ledgerAccount += ("P" + pspElement);
        else if (pspElement.length() == 9)
            ledgerAccount += ("K" + pspElement);
        return ledgerAccount;


    }

}

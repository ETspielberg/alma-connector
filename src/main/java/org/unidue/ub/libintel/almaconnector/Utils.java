package org.unidue.ub.libintel.almaconnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unidue.ub.alma.shared.acq.FundDistribution;
import org.unidue.ub.alma.shared.acq.Invoice;
import org.unidue.ub.alma.shared.acq.InvoiceLine;
import org.unidue.ub.alma.shared.acq.Vendor;
import org.unidue.ub.libintel.almaconnector.model.SapAccountData;
import org.unidue.ub.libintel.almaconnector.model.SapData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utils {

    private final static Logger log = LoggerFactory.getLogger(Utils.class);

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

    public final String calulateLedgerAccount(String fundCode) {
        String ledgerAccount = "6810";
        String fonds = "0000";
        String pspElement = "";

        String[] parts = fundCode.split("-");
        if (parts[0].startsWith("S")) {
            pspElement = parts[1];
            fonds = parts[2];
        } else if (parts[1].startsWith("1")) {
            fonds = parts[1];
            ledgerAccount = "6810" + parts[2];
        } else if (parts[1].startsWith("0")) {
            fonds = "0000";
            ledgerAccount = "6810" + parts[1];
            pspElement = "555100000" + "9" + parts[0];
        } else if (parts[1].startsWith("5")) {
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

    public static SapAccountData convertFundCodeToSapAccountData(String fundCode) {
        SapAccountData sapAccountData = new SapAccountData();
        String[] parts = fundCode.split("-");
        log.debug("decide upon " + parts[0]);
        if (parts[0].startsWith("S")) {
            log.debug("Sachmittel");
            if (parts[1].length() == 9)
                sapAccountData
                        .withCostCentre(parts[1])
                        .withFonds(parts[2]);
            else if (parts[1].length() == 14)
                sapAccountData
                        .withPspElement(parts[1])
                        .withLedgerAccount("6810" + parts[3])
                        .withFonds(parts[2]);
            if (parts[3].length() == 4)
                sapAccountData.setLedgerAccount("6810" + parts[3]);
            else
                sapAccountData.setLedgerAccount(parts[3]);

        } else if (parts[0].startsWith("1")) {
            log.debug("Berufungsmittel");
            sapAccountData
                    .withFonds(parts[1])
                    .withPspElement("555100000" + "9" + parts[0])
                    .withLedgerAccount("6810" + parts[2]);
        } else if (parts[0].startsWith("0")) {
            log.debug("Haushalt");
            sapAccountData
                    .withFonds("1000")
                    .withLedgerAccount("6810" + parts[1])
                    .withPspElement("555100000" + "9" + parts[0]);
        } else if (parts[0].startsWith("50")) {
            log.debug("QVM");
            sapAccountData
                    .withFonds("1400")
                    .withLedgerAccount("6810" + parts[1])
                    .withPspElement("555100000" + "9" + parts[0]);
        } else if (parts[0].startsWith("55")) {
            if (parts[1].equals("0")) {
                log.debug("Allgemeinmittel");
                sapAccountData
                        .withFonds("1000")
                        .withLedgerAccount("6810" + parts[2])
                        .withCostCentre(parts[0] + "0000");
            } else if (parts[1].equals("5")) {
                log.debug("Allgemeinmittel QVM");
                sapAccountData
                        .withFonds("1400")
                        .withLedgerAccount("6810" + parts[2])
                        .withCostCentre(parts[0] + "0000");
            }
        }
        return sapAccountData;
    }

}

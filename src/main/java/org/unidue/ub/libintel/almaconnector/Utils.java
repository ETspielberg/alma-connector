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

    /**
     * converts an Alma invoice and the corresponding vendor information into a set of SAP data
     * @param invoice the Alma Invoice object to be converted
     * @param vendor the Alma Vendor object for the invoice
     * @return a List of SapData, one for each fund to be used
     */
    public static List<SapData> convertInvoiceToSapData(Invoice invoice, Vendor vendor) {
        List<SapData> sapDataList = new ArrayList<>();
        if (invoice.getInvoiceLines() != null) {
            for (InvoiceLine invoiceLine : invoice.getInvoiceLines().getInvoiceLine()) {
                for (FundDistribution fundDistribution : invoiceLine.getFundDistribution()) {
                    String fundCode = fundDistribution.getFundCode().getValue();
                    SapAccountData sapAccountData = convertFundCodeToSapAccountData(fundCode);
                    if (sapAccountData == null) {
                        log.warn("no sap account available for fund " + fundDistribution.getFundCode().getValue());
                        continue;
                    }
                    SapData sapData = new SapData()
                            .withCurrency(invoice.getCurrency().getValue())
                            .withInvoiceAmount(fundDistribution.getAmount())
                            .withInvoiceDate(invoice.getInvoiceDate())
                            .withVendorCode(invoice.getVendor().getValue())
                            .withCreditor(vendor.getFinancialSysCode())
                            .withToDate(invoiceLine.getSubscriptionToDate())
                            .withFromDate(invoiceLine.getSubscriptionFromDate())
                            .withCommitmentDate(new Date())
                            .withCurrency(invoice.getCurrency().getValue())
                            .withPositionalNumber(invoiceLine.getNumber())
                            .withSapAccountData(sapAccountData)
                            .withInvoiceNumber(invoice.getNumber())
                            .withComment(invoiceLine.getNote());
                            // TO DO: .withCostType("TO BE DONE");
                    sapDataList.add(sapData);
                }
            }
        }
        return sapDataList;
    }

    /**
     * converts the fund code into a set of SAP-Data
     * @param fundCode the fund code to be converted
     * @return a SapAccountData object holding the individual SAP data
     */
    public static SapAccountData convertFundCodeToSapAccountData(String fundCode) {
        SapAccountData sapAccountData = new SapAccountData();
        // cut the string at the '-'
        String[] parts = fundCode.split("-");
        // if it is only one part, it is not a valid fund code. return null
        if (parts.length == 1)
            return null;
        log.debug("decide upon " + parts[0]);

        // first case: sachmittel starting with an S
        if (parts[0].startsWith("S")) {
            log.debug("Sachmittel");

            // if a cost centre is given (second part is nine fields long)
            if (parts[1].length() == 9)
                // set cost centre and fonds
                sapAccountData
                        .withCostCentre(parts[1])
                        .withFonds(parts[2]);

            // if a psp element is given (second part is 14 fields long)
            else if (parts[1].length() == 14)
                // set psp element and fonds
                sapAccountData
                        .withPspElement(parts[1])
                        .withFonds(parts[2]);

            // set the ledger account. if four digits are given us the 6810 prefix number
            if (parts[3].length() == 4)
                sapAccountData.setLedgerAccount("6810" + parts[3]);
            else
                sapAccountData.setLedgerAccount(parts[3]);

        //second case: Berufungsmittel (starting with 1)
        } else if (parts[0].startsWith("1")) {
            log.debug("Berufungsmittel");
            sapAccountData
                    .withFonds(parts[1])
                    .withPspElement("555100000" + "9" + parts[0])
                    .withLedgerAccount("6810" + parts[2]);

        //third case: Haushaltsmittel (starting with 0)
        } else if (parts[0].startsWith("0")) {
            log.debug("Haushalt");
            sapAccountData
                    .withFonds("1000")
                    .withLedgerAccount("6810" + parts[1])
                    .withPspElement("555100000" + "9" + parts[0]);

        // fourth case: QVM (starting with 50)
        } else if (parts[0].startsWith("50")) {
            log.debug("QVM");
            sapAccountData
                    .withFonds("1400")
                    .withLedgerAccount("6810" + parts[1])
                    .withPspElement("555100000" + "9" + parts[0]);

        // fifth case: Allgemeinmittel (starting with 55
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

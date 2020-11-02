package org.unidue.ub.libintel.almaconnector;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unidue.ub.alma.shared.acq.FundDistribution;
import org.unidue.ub.alma.shared.acq.Invoice;
import org.unidue.ub.alma.shared.acq.InvoiceLine;
import org.unidue.ub.alma.shared.acq.Vendor;
import org.unidue.ub.libintel.almaconnector.model.SapAccountData;
import org.unidue.ub.libintel.almaconnector.model.SapData;
import org.unidue.ub.libintel.almaconnector.model.SapResponse;
import org.unidue.ub.libintel.almaconnector.model.run.SapResponseRun;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * a number of static helper functions to convert data
 */
public class Utils {

    private final static Logger log = LoggerFactory.getLogger(Utils.class);

    public static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * converts an Alma invoice and the corresponding vendor information into a set of SAP data
     * @param invoice the Alma Invoice object to be converted
     * @param vendor the Alma Vendor object for the invoice
     * @return a List of SapData, one for each fund to be used
     */
    public static List<SapData> convertInvoiceToSapData(Invoice invoice, Vendor vendor) {
        // initialize the empty list
        List<SapData> sapDataList = new ArrayList<>();
        // check if there are invoices
        if (invoice.getInvoiceLines() != null) {
            log.debug("processing invoice " + invoice.getId());
            // go through the invoices
            for (InvoiceLine invoiceLine : invoice.getInvoiceLines().getInvoiceLine()) {
                log.debug("processing invoice line " + invoiceLine.getId());

                log.debug(String.format("found %d funds", invoiceLine.getFundDistribution().size()));
                boolean existsFunddistribution = invoiceLine.getFundDistribution().size() > 1;

                //go through the individual funds to create a single entry for each of the funds to be allocated
                for (int i = 0; i < invoiceLine.getFundDistribution().size(); i++) {
                    FundDistribution fundDistribution = invoiceLine.getFundDistribution().get(i);
                    // read the fund code
                    String fundCode = fundDistribution.getFundCode().getValue();
                    // convert the fund code into the corresponding SAP account data
                    SapAccountData sapAccountData = convertFundCodeToSapAccountData(fundCode);

                    // if the conversion fails, log the error
                    if (sapAccountData == null) {
                        log.warn("no sap account available for fund " + fundDistribution.getFundCode().getValue());
                        continue;
                    }

                    // create an SAP data object by the given data
                    SapData sapData = new SapData()
                            .withCurrency(invoice.getCurrency().getValue())
                            .withInvoiceAmount(fundDistribution.getAmount())
                            .withInvoiceDate(invoice.getInvoiceDate())
                            .withVendorCode(invoice.getVendor().getValue())
                            .withCreditor(vendor.getFinancialSysCode())
                            .withToDate(invoiceLine.getSubscriptionToDate())
                            .withFromDate(invoiceLine.getSubscriptionFromDate())
                            .withCommitmentDate(invoice.getPayment().getVoucherDate())
                            .withCurrency(invoice.getCurrency().getValue())
                            .withSapAccountData(sapAccountData)
                            .withInvoiceNumber(invoice.getNumber())
                            .withComment(invoiceLine.getNote());
                    if (existsFunddistribution) {
                        int positionalNumber = Integer.parseInt(invoiceLine.getNumber());
                        positionalNumber+= (i+1) *1000;
                        sapData.setPositionalNumber(String.valueOf(positionalNumber));
                    } else {
                        sapData.setPositionalNumber(invoiceLine.getNumber());
                    }

                    if ("EXCLUSIVE".equals(invoice.getInvoiceVat().getType().getValue())) {
                        double amount = invoiceLine.getPrice() * fundDistribution.getPercent() / 100;
                        sapData.setInvoiceAmount(amount);
                    }
                    // read the VAT code from the data.
                    try {
                        // get the vat code
                        String invoiceLineVatCode = invoiceLine.getInvoiceLineVat().getVatCode().getDesc();
                        if (invoiceLineVatCode.length() > 2)
                            invoiceLineVatCode = invoiceLineVatCode.substring(0,2);
                        // set the value of the vat code to a value which is not empty
                        if (!"".equals(invoiceLineVatCode)) {
                            sapData.costType = invoiceLineVatCode;
                            log.debug("set VAT code ot " + invoiceLineVatCode);
                        }
                        // if no vat code is set on the invoice line take the one from the invoice
                        else {
                            String invoiceVatCode = invoice.getInvoiceVat().getVatCode().getValue();
                            if (invoiceVatCode.length() > 2)
                                invoiceVatCode = invoiceVatCode.substring(0,2);
                            if (!"".equals(invoiceVatCode)) {
                                sapData.costType = invoiceLineVatCode;
                                log.debug("set VAT code to " + invoiceVatCode);
                            }
                            else {
                                log.warn("no vat code given for invoice line " + invoiceLine.getId());
                                sapData.costType = "";
                            }
                        }
                    } catch (Exception e) {
                        log.warn("no vat code given for invoice line " + invoiceLine.getId());
                        sapData.costType = "";
                    }
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

        // second case: Berufungsmittel (starting with 1)
        } else if (parts[0].startsWith("1")) {
            log.debug("Berufungsmittel");
            sapAccountData
                    .withFonds(parts[1])
                    .withPspElement("555100000" + "9" + parts[0])
                    .withLedgerAccount("6810" + parts[2]);

        // third case: Haushaltsmittel (starting with 0)
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

    /**
     * calculates a container holding the list of SAP response objects and the number of unsuccessful readings from an
     * excel file
     * @param worksheet a XSSFSheet object of a sheet in an excel file
     * @return a container object holding the number of errors upon reading individual lines and the list of read SAP
     * response objects
     */
    public static SapResponseRun getFromExcel(XSSFSheet worksheet, String filename) {
        SapResponseRun container = new SapResponseRun();
        container.setFilename(filename);
        // go through all lines except the first one (the headers) and the last one (summary of all invoices).
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows() - 1; i++) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            //read the row and get all the data from it.
            XSSFRow row = worksheet.getRow(i);
            String runId;
            try {
                runId = row.getCell(0).getStringCellValue();
            } catch (IllegalStateException ise) {
                runId = String.valueOf( row.getCell(0).getNumericCellValue());
            }
            String creditor;
            try {
                creditor =row.getCell(1).getStringCellValue();
            } catch (IllegalStateException ise) {
                creditor = String.valueOf( row.getCell(1).getNumericCellValue());
            }
            String invoiceId;
            try {
                invoiceId = row.getCell(2).getStringCellValue();
            } catch (IllegalStateException ise) {
                invoiceId = String.valueOf( row.getCell(2).getNumericCellValue());
            }
            String currency;
            try {
                currency = row.getCell(3).getStringCellValue();
            } catch (IllegalStateException ise) {
                currency = String.valueOf( row.getCell(3).getNumericCellValue());
            }
            double amount = 0.0;
            try {
                amount = row.getCell(4).getNumericCellValue();
            } catch (IllegalStateException ise) {
                container.increaseNumberOfReadErrors();
                log.warn("could not parse amount" + row.getCell(4).getStringCellValue(), ise);
            }
            String voucherId = row.getCell(5).getStringCellValue();
            SapResponse sapResponse = new SapResponse(runId, creditor, invoiceId, amount, currency, voucherId);
            Date from;
            try {
                from = row.getCell(6).getDateCellValue();
                sapResponse.setInvoiceFrom(from);
            } catch (IllegalStateException ise) {
                try {
                    from = format.parse(row.getCell(6).getStringCellValue());
                    sapResponse.setInvoiceFrom(from);
                } catch (ParseException pe) {
                    log.debug("no from date given in invoice " + invoiceId);
                }
            }
            Date to;
            try {
                to = row.getCell(7).getDateCellValue();
                sapResponse.setInvoiceFrom(to);
            } catch (IllegalStateException ise) {
                try {
                    to = format.parse(row.getCell(7).getStringCellValue());
                    sapResponse.setInvoiceFrom(to);
                } catch (ParseException pe) {
                    log.debug("no to date given in invoice " + invoiceId);
                }
            }
            // create the sap response object
            container.addSapResponse(sapResponse);
        }
        log.info(container.logString());
        return container;
    }
}

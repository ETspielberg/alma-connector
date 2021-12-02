package org.unidue.ub.libintel.almaconnector.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.acq.*;
import org.unidue.ub.libintel.almaconnector.clients.alma.analytics.AlmaAnalyticsReportClient;
import org.unidue.ub.libintel.almaconnector.model.analytics.InvoiceForPayment;
import org.unidue.ub.libintel.almaconnector.model.analytics.InvoiceForPaymentReport;
import org.unidue.ub.libintel.almaconnector.model.run.AlmaExportRun;
import org.unidue.ub.libintel.almaconnector.model.run.SapResponseRun;
import org.unidue.ub.libintel.almaconnector.model.sap.AvailableInvoice;
import org.unidue.ub.libintel.almaconnector.model.sap.SapAccountData;
import org.unidue.ub.libintel.almaconnector.model.sap.SapData;
import org.unidue.ub.libintel.almaconnector.model.sap.SapResponse;
import org.unidue.ub.libintel.almaconnector.repository.jpa.AlmaExportRunRepository;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaInvoiceService;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * offers functions around sap import and export data managed in alma
 *
 * @author Eike Spielberg
 * @author eike.spielberg@uni-due.de
 * @version 1.0
 */
@Service
@Slf4j
public class SapService {

    private final AlmaInvoiceService almaInvoiceService;

    private final AlmaExportRunRepository almaExportRunRepository;

    private final AlmaAnalyticsReportClient almaAnalyticsReportClient;

    public static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

    @Value("${sap.home.tax.keys}")
    private List<String> homeTaxKeys;

    private final String file;

    /**
     * constructor based autowiring of the Feign client
     *
     * @param dataDir the data directory where the export files are stored in
     * @param almaInvoiceService the Feign client for the Alma Invoice API
     * @param almaExportRunRepository the repository holding the data for an individual export run
     * @param almaAnalyticsReportClient the client to retrieve alma analytics reports
     *
     */
    SapService(@Value("${ub.statistics.data.dir}") String dataDir,
               AlmaInvoiceService almaInvoiceService,
               AlmaExportRunRepository almaExportRunRepository,
               AlmaAnalyticsReportClient almaAnalyticsReportClient) {
        this.file = dataDir + "/sapData/";
        this.almaInvoiceService = almaInvoiceService;
        this.almaExportRunRepository = almaExportRunRepository;
        this.almaAnalyticsReportClient = almaAnalyticsReportClient;
        File folder = new File(this.file);
        if (!folder.exists()) {
            if (!folder.mkdirs())
                log.warn("could not create data directory");
        }
    }

    /**
     * updates the Invoices in Alma with the results of the SAP import
     *
     * @param container the <class>SapResponseRun</class> object holding the data about this re-import session
     * @return the SAP container objects the number of missed entries
     */
    public SapResponseRun updateInvoiceWithErpData(SapResponseRun container) {
        HashMap<String, Boolean> poLines = new HashMap<>();
        for (SapResponse sapResponse : container.getResponses()) {
            // get the number of the invoice (is not the ID!)
            String invoiceId = sapResponse.getInvoiceNumber();

            // search the invoices for the invoice number
            Invoices invoices = this.almaInvoiceService.getInvoicesForInvocieId(invoiceId);

            log.debug(String.format("found %d invoices for invoice number %s", invoices.getTotalRecordCount(), invoiceId));
            // process only if there is only one invoice found. Otherwise increase number of errors and log the error
            if (invoices.getTotalRecordCount() == 1) {
                Invoice invoice = invoices.getInvoice().get(0);
                log.info("processing SAP response for invoice " + invoiceId);

                for (InvoiceLine invoiceLine : invoice.getInvoiceLines().getInvoiceLine())
                    if (poLines.containsKey(invoiceLine.getPoLine())) {
                        if (!invoiceLine.getFullyInvoiced()) {
                            poLines.put(invoiceLine.getPoLine(), false);
                        }
                    } else {
                        if (!invoiceLine.getFullyInvoiced()) {
                            poLines.put(invoiceLine.getPoLine(), false);
                        } else
                            poLines.put(invoiceLine.getPoLine(), true);
                    }

                // prepare the payment for the update
                Payment payment = invoice.getPayment();
                payment.setVoucherNumber(sapResponse.getVoucherNumber());
                payment.setVoucherAmount(String.valueOf(sapResponse.getAmount()));
                payment.setVoucherCurrency(new PaymentVoucherCurrency().value(sapResponse.getCurrency()));
                payment.setVoucherDate(new Date());

                // try to update the invoice.
                try {
                    // if the sum is the same as on the invoice, mark the invoice as paid and close the invoice
                    if (sapResponse.getAmount() == invoice.getTotalAmount()) {
                        this.almaInvoiceService.addFullPayment(invoice, payment);
                        log.info(String.format("closed invoice %s", invoiceId));
                        container.addClosedIncoice(invoiceId);
                        // otherwise just add the payment
                    } else {
                        container.addPartialInvoice(invoiceId);
                        log.info(String.format("added partial payment for invoice %s", invoiceId));
                        this.almaInvoiceService.addPartialPayment(invoice, payment);
                    }
                } catch (Exception e) {
                    container.addInvoiceWithError(invoiceId);
                    // if an Exception occurs (e.g. when trying to update the invoice), log the message and increase the number of errors.
                    log.error(String.format("could not update invoice %s", invoiceId));
                    container.increaseNumberOfErrors();
                    container.increaseNumberOfInvoiceErrors();
                }
            } else {
                // if none or more than one invoice is found log the message and increase the number of errors
                log.warn(String.format("found %d invoices for invoice number %s", invoices.getTotalRecordCount(), invoiceId));
                container.addInvoiceWithError(invoiceId);
                container.increaseNumberOfInvoiceErrors();
                container.increaseNumberOfErrors();
            }
        }
        return container;
    }

    /**
     * uses a provided alma analytics report to retrieve the open invoices to be exported
     * @param almaExportRun the <class>AlmaExportRun</class> object holding the data about this export session
     * @return a list of invoices
     */
    public List<Invoice> getOpenInvoicesFromAnalytics(AlmaExportRun almaExportRun) {
        try {
            List<InvoiceForPayment> result = this.almaAnalyticsReportClient.getReport(InvoiceForPaymentReport.PATH, InvoiceForPaymentReport.class).getRows();
            List<Invoice> invoices = new ArrayList<>();
            if (result != null) {
                for (InvoiceForPayment invoiceEntry : result) {
                    if (invoiceEntry.getInvoiceOwnerCode().equals(almaExportRun.getInvoiceOwner())) {
                        Invoice invoiceInd = this.almaInvoiceService.retrieveInvoice(invoiceEntry.getInvoiceNumber());
                        almaExportRun.addInvoice(invoiceInd);
                        List<SapData> sapDataList = convertToSapData(invoiceInd, invoiceEntry.getErpCode(), invoiceEntry.getOrderLineType());
                        log.debug(String.format("adding %d SAP data to the list", sapDataList.size()));
                        almaExportRun.addSapDataList(sapDataList, homeTaxKeys);
                        log.debug(String.format("run contains now %d entries: %d home and %d foreign",
                                almaExportRun.getTotalSapData(),
                                almaExportRun.getHomeSapData().size(),
                                almaExportRun.getForeignSapData().size()));
                    }
                }
            }
            return invoices;
        } catch (IOException ioe) {
            log.error("could not connect to analytics");
            return null;
        }
    }

    /**
     * writes the SAP data contained in an AlmaExportRun object as files to disk
     *
     * @param almaExportRun the <class>AlmaExportRun</class> object holding the data about this export session
     * @return the AlmaExportRun object updated with the files created and the number of failed entries to be written
     */
    public AlmaExportRun writeAlmaExport(AlmaExportRun almaExportRun) {
        String dateString;
        if (almaExportRun.isDateSpecific())
            dateString = dateformat.format(almaExportRun.getDesiredDate());
        else
            dateString = dateformat.format(new Date());
        String checkFilename = String.format("Druck-sap_%s_%s_%s.txt", "all", dateString, almaExportRun.getInvoiceOwner());
        String homeFilename = String.format("sap_%s_%s_%s.txt", "home", dateString, almaExportRun.getInvoiceOwner());
        String foreignFilename = String.format("sap_%s_%s_%s.txt", "foreign", dateString, almaExportRun.getInvoiceOwner());
        initializeFiles(dateString, checkFilename, homeFilename, foreignFilename);

        for (SapData sapData : almaExportRun.getHomeSapData()) {
            if (!sapData.isChecked)
                continue;
            try {
                addLineToFile(checkFilename, sapData.toFixedLengthLine());
            } catch (IOException ex) {
                log.warn("could not write line: " + sapData.toFixedLengthLine());
            }
            try {
                addLineToFile(homeFilename, generateComment(sapData).toCsv());
            } catch (IOException ex) {
                almaExportRun.increaseMissedSapData();
                almaExportRun.addMissedSapData(sapData);
                log.warn("could not write line: " + sapData.toFixedLengthLine());
            }
        }
        for (SapData sapData : almaExportRun.getForeignSapData()) {
            if (!sapData.isChecked)
                continue;
            try {
                addLineToFile(checkFilename, sapData.toFixedLengthLine());
            } catch (IOException ex) {
                log.warn("could not write line: " + sapData.toFixedLengthLine());
            }
            try {
                addLineToFile(foreignFilename, generateComment(sapData).toCsv());
            } catch (IOException ex) {
                almaExportRun.increaseMissedSapData();
                almaExportRun.addMissedSapData(sapData);
                log.warn("could not write line: " + sapData.toFixedLengthLine());
            }
        }
        this.almaExportRunRepository.save(almaExportRun);
        return almaExportRun;
    }

    /**
     * loads the sap files from  disk
     *
     * @param date the date for which the file shall be obtained
     * @param type the type of the file to be obtained (HomeCurrency or ForeignCurrency)
     * @return the file as Resource object
     * @throws FileNotFoundException thrown if the file could not be loaded
     */
    public org.springframework.core.io.Resource loadFiles(String date, String type, String owner) throws FileNotFoundException {
        String filename = String.format("sap_%s_%s_%s.txt", type, date, owner);
        Path file = Paths.get(this.file + filename);
        try {
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new FileNotFoundException(
                        "Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            log.error("could not read file: " + filename, e);
            return null;
        }
    }

    /**
     * converts an alma invoce to a list of sap data to be imported.
     *
     * @param invoice   the Alma Invoice object
     * @param erpCode   the sap creditor code
     * @param orderType the type of order
     * @return a list of SapData objects
     */
    public static List<SapData> convertToSapData(Invoice invoice, String erpCode, String orderType) {
        List<SapData> sapDataList = new ArrayList<>();
        // check if there are invoices
        if (invoice.getInvoiceLines() != null) {
            log.debug("processing invoice " + invoice.getId());
            // go through the invoices

            int positionalNumber = 1;
            for (InvoiceLine invoiceLine : invoice.getInvoiceLines().getInvoiceLine()) {
                log.debug("processing invoice line " + invoiceLine.getId());

                log.debug(String.format("found %d funds", invoiceLine.getFundDistribution().size()));

                //go through the individual funds to create a single entry for each of the funds to be allocated
                for (FundDistribution fundDistribution : invoiceLine.getFundDistribution()) {
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
                            .withCreditor(erpCode)
                            .withToDate(invoiceLine.getSubscriptionToDate())
                            .withFromDate(invoiceLine.getSubscriptionFromDate())
                            .withPositionalNumber(String.valueOf(positionalNumber))
                            .withCommitmentDate(invoice.getPayment().getVoucherDate())
                            .withCurrency(invoice.getCurrency().getValue())
                            .withSapAccountData(sapAccountData)
                            .withInvoiceNumber(invoice.getNumber())
                            .withComment(invoiceLine.getPriceNote());
                    positionalNumber++;


                    if ("EXCLUSIVE".equals(invoice.getInvoiceVat().getType().getValue())) {
                        double amount = invoiceLine.getPrice() * fundDistribution.getPercent() / 100;
                        sapData.setInvoiceAmount(amount);
                    }

                    // read the VAT code from the data.
                    try {
                        // get the vat code
                        String invoiceLineVatCode = invoiceLine.getInvoiceLineVat().getVatCode().getDesc();
                        if (invoiceLineVatCode.length() > 2)
                            invoiceLineVatCode = invoiceLineVatCode.substring(0, 2);
                        // set the value of the vat code to a value which is not empty
                        if (!"".equals(invoiceLineVatCode)) {
                            sapData.costType = invoiceLineVatCode;
                            log.debug("set VAT code to " + invoiceLineVatCode);
                        }
                        // if no vat code is set on the invoice line take the one from the invoice
                        else {
                            String invoiceVatCode = invoice.getInvoiceVat().getVatCode().getValue();
                            if (invoiceVatCode.length() > 2)
                                invoiceVatCode = invoiceVatCode.substring(0, 2);
                            if (!"".equals(invoiceVatCode)) {
                                sapData.costType = invoiceLineVatCode;
                                log.debug("set VAT code to " + invoiceVatCode);
                            } else {
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
     * converts an Alma invoice and the corresponding vendor information into a set of SAP data
     *
     * @param invoice the Alma Invoice object to be converted
     * @param vendor  the Alma Vendor object for the invoice
     * @return a List of SapData, one for each fund to be used
     */
    public static List<SapData> convertInvoiceToSapData(Invoice invoice, Vendor vendor) {
        // initialize the empty list
        List<SapData> sapDataList = new ArrayList<>();
        // check if there are invoices
        if (invoice.getInvoiceLines() != null) {
            log.debug("processing invoice " + invoice.getId());
            // go through the invoices

            int positionalNumber = 1;
            for (InvoiceLine invoiceLine : invoice.getInvoiceLines().getInvoiceLine()) {
                log.debug("processing invoice line " + invoiceLine.getId());

                log.debug(String.format("found %d funds", invoiceLine.getFundDistribution().size()));

                //go through the individual funds to create a single entry for each of the funds to be allocated
                for (FundDistribution fundDistribution : invoiceLine.getFundDistribution()) {
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
                            .withPositionalNumber(String.valueOf(positionalNumber))
                            .withCommitmentDate(invoice.getPayment().getVoucherDate())
                            .withCurrency(invoice.getCurrency().getValue())
                            .withSapAccountData(sapAccountData)
                            .withInvoiceNumber(invoice.getNumber())
                            .withComment(invoiceLine.getNote());
                    positionalNumber++;


                    if ("EXCLUSIVE".equals(invoice.getInvoiceVat().getType().getValue())) {
                        double amount = invoiceLine.getPrice() * fundDistribution.getPercent() / 100;
                        sapData.setInvoiceAmount(amount);
                    }
                    // read the VAT code from the data.
                    try {
                        // get the vat code
                        String invoiceLineVatCode = invoiceLine.getInvoiceLineVat().getVatCode().getDesc();
                        if (invoiceLineVatCode.length() > 2)
                            invoiceLineVatCode = invoiceLineVatCode.substring(0, 2);
                        // set the value of the vat code to a value which is not empty
                        if (!"".equals(invoiceLineVatCode)) {
                            sapData.costType = invoiceLineVatCode;
                            log.debug("set VAT code ot " + invoiceLineVatCode);
                        }
                        // if no vat code is set on the invoice line take the one from the invoice
                        else {
                            String invoiceVatCode = invoice.getInvoiceVat().getVatCode().getValue();
                            if (invoiceVatCode.length() > 2)
                                invoiceVatCode = invoiceVatCode.substring(0, 2);
                            if (!"".equals(invoiceVatCode)) {
                                sapData.costType = invoiceLineVatCode;
                                log.debug("set VAT code to " + invoiceVatCode);
                            } else {
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
     *
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
        } else if (parts[0].startsWith("C")) {
            log.debug("Sonderfonds Corona-Soforthilfe");

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
        } else if (parts[0].startsWith("50") || parts[0].startsWith("51")) {
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
     *
     * @param worksheet a XSSFSheet object of a sheet in an excel file
     * @return a container object holding the number of errors upon reading individual lines and the list of read SAP
     * response objects
     */
    public static SapResponseRun getFromExcel(XSSFSheet worksheet, String filename) {
        // prepare the sap response run container object
        SapResponseRun container = new SapResponseRun();

        // add the filename information to the container
        container.setFilename(filename);

        //initialize the hashmap to collect the lines for each invoice
        HashMap<String, SapResponse> sapResponses = new HashMap<>();

        // go through all lines except the first one (the headers) and the last one (summary of all invoices).
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows() - 1; i++) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            //read the row and get all the data from it.
            XSSFRow row = worksheet.getRow(i);

            // read the runId
            String runId;
            try {
                runId = row.getCell(0).getStringCellValue();
            } catch (IllegalStateException ise) {
                runId = String.valueOf(row.getCell(0).getNumericCellValue());
            }

            // read the creditor
            String creditor;
            try {
                creditor = row.getCell(1).getStringCellValue();
            } catch (IllegalStateException ise) {
                creditor = String.valueOf(row.getCell(1).getNumericCellValue());
            }

            // read the invoice ID
            String invoiceId;
            try {
                invoiceId = row.getCell(2).getStringCellValue();
            } catch (IllegalStateException ise) {
                invoiceId = String.valueOf(row.getCell(2).getNumericCellValue());
            }

            // read the currency
            String currency;
            try {
                currency = row.getCell(3).getStringCellValue();
            } catch (IllegalStateException ise) {
                currency = String.valueOf(row.getCell(3).getNumericCellValue());
            }

            // read the amount
            double amount = 0.0;
            try {
                amount = row.getCell(4).getNumericCellValue();
            } catch (IllegalStateException ise) {
                container.increaseNumberOfReadErrors();
                log.warn("could not parse amount" + row.getCell(4).getStringCellValue(), ise);
            }

            // read the voucher ID
            String voucherId;
            try {
                voucherId = row.getCell(5).getStringCellValue();
            } catch (IllegalStateException ise) {
                voucherId = String.valueOf(row.getCell(5).getNumericCellValue());
            }

            // if the invoice is already in the hashmap just add the corresponding amount
            if (sapResponses.containsKey(invoiceId)) {
                sapResponses.get(invoiceId).addAmount(amount);
            } else {
                // create a new SAP response object
                SapResponse sapResponse = new SapResponse(runId, creditor, invoiceId, amount, currency, voucherId);

                // read in the from date if given
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

                // read in the to date if given
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
                // add the invoice to the hashmap
                sapResponses.put(invoiceId, sapResponse);
            }
        }
        // add the sap response objects to the response run container
        for (SapResponse sapResponse : sapResponses.values())
            if (!sapResponse.getInvoiceNumber().isEmpty())
                container.addSapResponse(sapResponse);
        log.info(container.logString());
        return container;
    }


    /**
     * retrieves a list of invoices for a given export session
     * @param almaExportRun the <class>AlmaExportRun</class> object holding the data about this export session
     * @return the <class>AlmaExportRun</class> object holding the data about this export session and the retrieved invoices
     */
    public AlmaExportRun getInvoices(AlmaExportRun almaExportRun) {
        List<Invoice> invoices;
        if (almaExportRun.isDateSpecific()) {
            log.info("collecting invoices for date");
            invoices = almaInvoiceService.getOpenInvoicesForDate(almaExportRun.getDesiredDate(), almaExportRun.getInvoiceOwner());
        } else {
            log.info("collecting all invoices");
            invoices = almaInvoiceService.getOpenInvoices(almaExportRun.getInvoiceOwner());
        }
        log.info("retrieved " + invoices.size() + " (filtered) invoices");
        almaExportRun.setInvoices(invoices);
        almaExportRun.setLastRun(new Date());
        this.almaExportRunRepository.save(almaExportRun);
        return almaExportRun;
    }

    private void addLineToFile(String filename, String line) throws IOException {
        log.info("writing line \n" + line);
        BufferedWriter bw = new BufferedWriter(new FileWriter(this.file + filename, true));
        bw.write(line);
        bw.newLine();
        bw.flush();
        bw.close();
    }

    private void addLine(BufferedWriter bw, String line) throws IOException {
        bw.write(line);
        bw.newLine();
        bw.flush();
    }

    private void initializeFiles(String currentDate, String checkFilename, String sapFilename, String foreignFilename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.file + checkFilename, false))) {
            addLine(bw, "Kontrollausdruck der SAP-Datei, Bearbeitungsdatum: " + currentDate);
        } catch (IOException ioe) {
            log.warn("could not create empty check file at " + currentDate, ioe);
        }
        initializeSapFiles(sapFilename);
        initializeSapFiles(foreignFilename);
    }

    private void initializeSapFiles(String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.file + filename, false))) {
            bw.write("");
            bw.flush();
        } catch (IOException ioe) {
            log.warn("could not create empty sap file.", ioe);
        }
    }

    private SapData generateComment(SapData sapData) {
        if (sapData.getComment() != null && !sapData.getComment().trim().isEmpty())
            return sapData;
        switch (sapData.sapAccountData.getImportCheckString()) {
            case "681004002020P55300000030002":
            case "681010002020P55300000030002":
            case "681000002020P40100000130010":
            case "681002002020P55300000030002":
            case "681000002020P55300000030002":
            case "681005002020P55300000030002":
                break;
            default: {
                if ("B-HBZ".equals(sapData.vendorCode)) {
                    return sapData;
                }
                switch (sapData.sapAccountData.getLedgerAccount()) {
                    case "68100000": {
                        sapData.comment = "Monographien";
                        break;
                    }
                    case "68100010": {
                        sapData.comment = "Monographien, Verbrauch";
                        break;
                    }
                    case "68100200": {
                        sapData.comment = "Zeitschriften-Abo";
                        break;
                    }
                    case "68100210": {
                        sapData.comment = "Zeitschriften-Abo Verbrauch";
                        break;
                    }
                    case "68100300": {
                        sapData.comment = "Fortsetzungen";
                        break;
                    }
                    case "68100400": {
                        sapData.comment = "Elektron. Zeitschr., Kauf";
                        break;
                    }
                    case "68100500": {
                        sapData.comment = "Elektron. Zeitschr., Lizenz";
                        break;
                    }
                    case "68100600": {
                        sapData.comment = "Datenbanken, laufend/Kauf";
                        break;
                    }
                    case "68100700": {
                        sapData.comment = "Datenbanken, laufend/Lizenz";
                        break;
                    }
                    case "68100800": {
                        sapData.comment = "Datenbanken, einmalig/Kauf";
                        break;
                    }
                    case "68100900": {
                        sapData.comment = "Datenbanken, einmalig/Lizenz";
                        break;
                    }
                    case "68101000": {
                        sapData.comment = "Sonst. Non-Book-Materialien";
                        break;
                    }
                    case "68101100": {
                        sapData.comment = "Einband";
                        break;
                    }
                    case "68101200": {
                        sapData.comment = "Bestandserhaltung";
                        break;
                    }
                    case "68101900": {
                        sapData.comment = "Sonst. Literaturkosten";
                        break;
                    }
                    case "68910100": {
                        sapData.comment = "Aufwendungen f. Veroeffentlichungen";
                        break;
                    }
                    default:
                        sapData.comment = "";
                }
            }
        }
        return sapData;
    }

    public AlmaExportRun getInvoicesForInvoiceNumbers(AlmaExportRun almaExportRun) {
        List<Invoice> invoices;
        log.info("collecting all invoices for given invoice numbers");
        invoices = almaInvoiceService.getInvocesByInvoiceNumber(almaExportRun.getAvailableInvoices());
        log.info("retrieved " + invoices.size() + " invoices by invoice numbers");
        almaExportRun.setInvoices(invoices);
        almaExportRun.setLastRun(new Date());
        this.almaExportRunRepository.save(almaExportRun);
        return almaExportRun;
    }

    public AlmaExportRun getAvailableInvoices(AlmaExportRun almaExportRun) {
        List<AvailableInvoice> invoices = almaInvoiceService.getAvailableInvoices(almaExportRun.getInvoiceOwner());
        log.info("retrieved " + invoices.size() + " (filtered) invoices");
        almaExportRun.setAvailableInvoices(invoices);
        almaExportRun.setLastRun(new Date());
        this.almaExportRunRepository.save(almaExportRun);
        return almaExportRun;
    }
}

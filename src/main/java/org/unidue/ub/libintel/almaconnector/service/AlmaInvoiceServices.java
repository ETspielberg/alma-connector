package org.unidue.ub.libintel.almaconnector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.acq.*;
import org.unidue.ub.libintel.almaconnector.clients.acquisition.AlmaInvoicesApiClient;
import org.unidue.ub.libintel.almaconnector.clients.analytics.AlmaAnalyticsReportClient;
import org.unidue.ub.libintel.almaconnector.model.InvoiceUpdate;
import org.unidue.ub.libintel.almaconnector.model.SapData;
import org.unidue.ub.libintel.almaconnector.model.SapResponse;
import org.unidue.ub.libintel.almaconnector.model.analytics.InvoiceForPayment;
import org.unidue.ub.libintel.almaconnector.model.analytics.InvoicesForPaymentAnalyticsResult;
import org.unidue.ub.libintel.almaconnector.model.run.AlmaExportRun;
import org.unidue.ub.libintel.almaconnector.model.run.SapResponseRun;
import org.unidue.ub.libintel.almaconnector.repository.AlmaExportRunRepository;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.unidue.ub.libintel.almaconnector.Utils.convertToSapData;

@Service
public class AlmaInvoiceServices {

    private final AlmaInvoicesApiClient almaInvoicesApiClient;

    private final AlmaExportRunRepository almaExportRunRepository;

    private final AlmaAnalyticsReportClient almaAnalyticsReportClient;

    @Value("${sap.home.tax.keys}")
    private List<String> homeTaxKeys;

    private final static String reportPath = "/shared/Universität Duisburg-Essen 49HBZ_UDE/Rechnungen zur Bezahlung";

    private final static Logger log = LoggerFactory.getLogger(AlmaInvoiceServices.class);

    /**
     * constructor based autowiring of the Feign client
     *
     * @param almaInvoicesApiClient the Feign client for the Alma Invoice API
     */
    AlmaInvoiceServices(AlmaInvoicesApiClient almaInvoicesApiClient,
                        AlmaExportRunRepository almaExportRunRepository,
                        AlmaAnalyticsReportClient almaAnalyticsReportClient) {
        this.almaInvoicesApiClient = almaInvoicesApiClient;
        this.almaExportRunRepository = almaExportRunRepository;
        this.almaAnalyticsReportClient = almaAnalyticsReportClient;
    }

    /**
     * collects the invoices from alma and saves them to the alma export run object
     *
     * @param almaExportRun the alma export run object containing the data for the invoices to collect
     * @return the alma export run object holding the list of invoices
     */
    @Secured({"ROLE_SYSTEM", "ROLE_SAP", "ROLE_ALMA_Invoice Operator Extended"})
    public AlmaExportRun getInvoices(AlmaExportRun almaExportRun) {
        List<Invoice> invoices;
        if (almaExportRun.isDateSpecific()) {
            log.info("collecting invoices for date");
            invoices = getOpenInvoicesForDate(almaExportRun.getDesiredDate(), almaExportRun.getInvoiceOwner());
        } else {
            log.info("collecting all invoices");
            invoices = getOpenInvoices(almaExportRun.getInvoiceOwner());
        }
        log.info("retrieved " + invoices.size() + " (filtered) invoices");
        almaExportRun.setInvoices(invoices);
        almaExportRun.setLastRun(new Date());
        this.almaExportRunRepository.save(almaExportRun);
        return almaExportRun;
    }

    public List<Invoice> getOpenInvoicesFromAnalytics(AlmaExportRun almaExportRun) {
        try {
            InvoicesForPaymentAnalyticsResult result = this.almaAnalyticsReportClient.getReport(reportPath, InvoicesForPaymentAnalyticsResult.class);
            List<Invoice> invoices = new ArrayList<>();
            if (result.getRows() != null) {
                for (InvoiceForPayment invoiceEntry: result.getRows()) {
                    if (invoiceEntry.getInvoiceOwnerCode().equals(almaExportRun.getInvoiceOwner())) {
                        Invoice invoiceInd = this.almaInvoicesApiClient.getInvoicesInvoiceId("application/json", invoiceEntry.getInvoiceNumber(), "full");
                        almaExportRun.addInvoice(invoiceInd);
                        List<SapData> sapDataList = convertToSapData(invoiceInd, invoiceEntry.getErpCode(), invoiceEntry.getOrderLineType());
                        log.debug(String.format("adding %d SAP data to the list", sapDataList.size()));
                        almaExportRun.addSapDataList(sapDataList, homeTaxKeys);
                        log.debug(String.format("run contains now %d entries: %d home and %d foreign",
                                almaExportRun.getTotalSapData(),
                                almaExportRun.getHomeSapData().size(),
                                almaExportRun.getForeignSapData().size() ));
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
     * retrieves the open invoices from the Alma API.
     *
     * @return a list of invoices
     */
    @Secured({"ROLE_SYSTEM", "ROLE_SAP", "ROLE_ALMA_Invoice Operator Extended"})
    public List<Invoice> getOpenInvoices(String owner) {
        // initialize parameters
        int batchSize = 25;
        int offset = 0;

        // retrieve first list of invocies.
        Invoices invoices = this.almaInvoicesApiClient.getInvoices("application/json", "ACTIVE", "Waiting to be Sent", owner, "", "", batchSize, offset, "");
        List<Invoice> invoiceList = new ArrayList<>(invoices.getInvoice());

        log.debug("retrieving " + invoices.getTotalRecordCount() + " invoices");

        // as long as not all data are being collected, collect further
        while (offset < invoices.getTotalRecordCount()) {
            offset += batchSize;
            log.debug("collecting invoices from " + offset + " to " + (offset + batchSize));
            invoices = this.almaInvoicesApiClient.getInvoices("application/json", "ACTIVE", "Waiting to be Sent", owner, "", "", batchSize, offset, "");
            invoiceList.addAll(invoices.getInvoice());
        }
        log.debug(String.format("retrieved list of %d invoices", invoiceList.size()));
        return invoiceList;
    }

    /**
     * returns a list of open invoices for a given date
     *
     * @param date the date invoices should be returned for
     * @return a list of invoices
     */
    @Secured({"ROLE_SYSTEM", "ROLE_SAP", "ROLE_ALMA_Invoice Operator Extended"})
    public List<Invoice> getOpenInvoicesForDate(Date date, String owner) {
        log.info("collecting invoices for date " + new SimpleDateFormat("dd.MM.yyyy").format(date));
        return filterList(date, getOpenInvoices(owner));
    }

    /**
     * updates the Invoices in Alma with the results of the SAP import
     *
     * @param container an SAP container object holding a list of SAP response object
     * @return the SAP container objects the number of missed entries
     */
    @Secured({"ROLE_SYSTEM", "ROLE_SAP", "ROLE_ALMA_Invoice Operator Extended"})
    public SapResponseRun updateInvoiceWithErpData(SapResponseRun container) {
        for (SapResponse sapResponse : container.getResponses()) {
            // get the number of the invoice (is not the ID!)
            String invoiceId = sapResponse.getInvoiceNumber();

            // search the invoices for the invoice number
            String searchQuery = "invoice_number~" + invoiceId;
            Invoices invoices = this.almaInvoicesApiClient.getInvoices("application/json", "ACTIVE",
                    "Waiting to be Sent", "", "", searchQuery, 20, 0, "");

            log.debug(String.format("found %d invoices for invoice number %s", invoices.getTotalRecordCount(), invoiceId));
            // process only if there is only one invoice found. Otherwise increase number of errors and log the error
            if (invoices.getTotalRecordCount() == 1) {
                Invoice invoice = invoices.getInvoice().get(0);
                log.info("processing SAP responce for Invoice " + invoiceId);

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
                        payment.setPaymentStatus(new PaymentPaymentStatus().value("PAID").desc("bezahlt"));
                        InvoiceUpdate invoiceUpdate = new InvoiceUpdate(payment);
                        this.almaInvoicesApiClient.postInvoicesInvoiceIdToUpdate(invoiceUpdate, "application/json", invoice.getId(), "paid");

                    // otherwise just add the payment
                    } else {
                        InvoiceUpdate invoiceUpdate = new InvoiceUpdate(payment);
                        this.almaInvoicesApiClient.postInvoicesInvoiceIdToUpdate(invoiceUpdate, "application/json", invoice.getId(), "");
                    }
                } catch (Exception e) {
                    // if an Exception occurs (e.g. when trying to update the invoice), log the message and increase the number of errors.
                    log.error(String.format("could not update invoice %s", invoiceId));
                    container.increaseNumberOfErrors();
                }
            } else {
                // if none or more than one invoice is found log the message and increase the number of errors
                log.warn(String.format("found %d invoices for invoice number %s", invoices.getTotalRecordCount(), invoiceId));
                container.increaseNumberOfErrors();
            }
        }
        log.info(container.logString());
        return container;
    }

    public Invoice saveInvoice(Invoice invoice) {
        return this.almaInvoicesApiClient.postAcqInvoices(invoice, "application/json");
    }

    /**
     * filters a list of Invoices according a given voucher date
     *
     * @param date     the date of the voucher date to be returned
     * @param invoices the list of invoices to be filtered
     * @return the filtered list of invoices
     */
    private List<Invoice> filterList(Date date, List<Invoice> invoices) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        List<Invoice> filteredInvoices = new ArrayList<>();
        for (Invoice invoice : invoices)
            if (invoice.getPayment() != null) {
                log.info("checking invoice from " + dateFormat.format(invoice.getPayment().getVoucherDate()));
                if (dateFormat.format(invoice.getPayment().getVoucherDate()).equals(dateFormat.format(date))) {
                    log.info("found invoice for date " + dateFormat.format(date));
                    filteredInvoices.add(invoice);
                }
            } else {
                log.warn("no voucher date given for invoice " + invoice.getId());
            }
        return filteredInvoices;
    }

    public void addInvoiceLine(String id, InvoiceLine invoiceLine) {
        this.almaInvoicesApiClient.postInvoicesInvoiceIdLines(invoiceLine, "application/json", id);
    }

    public void processInvoice(String id) {
        this.almaInvoicesApiClient.postInvoicesInvoiceId(new Invoice(), "application/json", id, "process_invoice");
    }
}

package org.unidue.ub.libintel.almaconnector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.acq.*;
import org.unidue.ub.libintel.almaconnector.clients.acquisition.AlmaInvoicesApiClient;
import org.unidue.ub.libintel.almaconnector.model.run.SapResponseRun;
import org.unidue.ub.libintel.almaconnector.model.run.AlmaExportRun;
import org.unidue.ub.libintel.almaconnector.model.InvoiceUpdate;
import org.unidue.ub.libintel.almaconnector.model.SapResponse;
import org.unidue.ub.libintel.almaconnector.repository.AlmaExportRunRepository;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AlmaInvoiceServices {

    private final AlmaInvoicesApiClient almaInvoicesApiClient;

    private final AlmaExportRunRepository almaExportRunRepository;

    private final static Logger log = LoggerFactory.getLogger(AlmaInvoiceServices.class);

    /**
     * constructor based autowiring of the Feign client
     * @param almaInvoicesApiClient the Feign client for the Alma Invoice API
     */
    AlmaInvoiceServices(AlmaInvoicesApiClient almaInvoicesApiClient, AlmaExportRunRepository almaExportRunRepository) {
        this.almaInvoicesApiClient = almaInvoicesApiClient;
        this.almaExportRunRepository = almaExportRunRepository;
    }

    /**
     * collects the invoices from alma and saves them to the alma export run object
     * @param almaExportRun the alma export run object containing the data for the invoices to collect
     * @return the alma export run object holding the list of invoices
     */
    @Secured({ "ROLE_SYSTEM", "ROLE_SAP" })
    public AlmaExportRun getInvoices(AlmaExportRun almaExportRun) {
        List<Invoice> invoices;
        if (almaExportRun.isDateSpecific()) {
            log.info("collecting invoices for date");
            invoices = getOpenInvoicesForDate(almaExportRun.getDesiredDate());
        } else {
            log.info("collecting all invoices");
            invoices = getOpenInvoices();
        }
        log.info("retrieved " + invoices.size() + " (filtered) invoices");
        almaExportRun.setInvoices(invoices);
        almaExportRun.setLastRun(new Date());
        this.almaExportRunRepository.save(almaExportRun);
        return almaExportRun;
    }

    /**
     * retrieves the open invoices from the Alma API.
     * @return  a list of invoices
     */
    @Secured({ "ROLE_SYSTEM", "ROLE_SAP" })
    public List<Invoice> getOpenInvoices() {
        // initialize parameters
        int batchSize = 25;
        int offset = 0;

        // retrieve first list of po-lines.
        Invoices invoices = this.almaInvoicesApiClient.getInvoices("application/json", "ACTIVE", "Ready to be Paid", "", "", "", batchSize, offset, "");
        List<Invoice> invoiceList = new ArrayList<>(invoices.getInvoice());

        log.info("retrieving " + invoices.getTotalRecordCount() + " invoices");

        // as long as not all data are being collected, collect further
        while (invoiceList.size() < invoices.getTotalRecordCount()) {
            offset += batchSize;
            log.info("collecting invoices from " + offset + " to " + (offset + batchSize));
            invoices = this.almaInvoicesApiClient.getInvoices("application/json", "ACTIVE", "Ready to be Paid", "", "", "", batchSize, offset, "");
            invoiceList.addAll(invoices.getInvoice());
        }
        log.info(String.format("retrieved list of %d invoices", invoiceList.size()));
        return invoiceList;
    }

    /**
     * returns a list of open invoices for a given date
     * @param date the date invoices should be returned for
     * @return a list of invoices
     */
    @Secured({ "ROLE_SYSTEM", "ROLE_SAP" })
    public List<Invoice> getOpenInvoicesForDate(Date date) {
        log.info("collecting invoices for date " + new SimpleDateFormat("dd.MM.yyyy").format(date));
        return filterList(date, getOpenInvoices());
    }

    /**
     * updates the Invoices in Alma with the results of the SAP import
     * @param container an SAP container object holding a list of SAP response object
     * @return the SAP container objects the number of missed entries
     */
    @Secured({ "ROLE_SYSTEM", "ROLE_SAP" })
    public SapResponseRun updateInvoiceWithErpData(SapResponseRun container) {
        List<SapResponse> sapResponses = container.getResponses();
        log.debug("got " + sapResponses.size() + "SAP responses");
        Map<String, List<SapResponse>> sapResponsesPerInvoice = new HashMap<>();

        // first order all items in a hashmap to collect the individual responses grouped together to rebuild the
        // invoices from the invoice lines.
        for (SapResponse sapResponse : sapResponses) {
            String invoiceNumber = sapResponse.getInvoiceNumber();
            if (sapResponsesPerInvoice.containsKey(invoiceNumber)) {
                log.debug("invoice number already in map, adding sap response to list");
                sapResponsesPerInvoice.get(invoiceNumber).add(sapResponse);
            } else {
                sapResponsesPerInvoice.put(invoiceNumber, new ArrayList<>(Collections.singletonList(sapResponse)));
                log.debug("invoice number not in map, creating new entry");
            }
        }
        for (Map.Entry<String, List<SapResponse>> entry : sapResponsesPerInvoice.entrySet()) {
            String searchQuery = "invoice_number~" + entry.getKey();
            Invoices invoices = this.almaInvoicesApiClient.getInvoices("application/json", "ACTIVE", "Ready to be Paid", "", "", searchQuery, 20, 0, "");
            if (invoices.getTotalRecordCount() == 1) {
                Invoice invoice = invoices.getInvoice().get(0);
                List<SapResponse> indiviudalResponses = entry.getValue();
                log.info("Invoice " + entry.getKey() + ": got " + indiviudalResponses.size() + " + vouchers for " + invoice.getInvoiceLines().getInvoiceLine().size() + " invoices lines");
                if (invoice.getInvoiceLines().getInvoiceLine().size() == indiviudalResponses.size()) {
                    Payment payment = invoice.getPayment();
                    double totalAmount = 0.0;
                    for (SapResponse individualResponse: indiviudalResponses) {
                        totalAmount += individualResponse.getAmount();
                    }
                    InvoiceUpdate invoiceUpdate = new InvoiceUpdate(payment);
                    try {
                        this.almaInvoicesApiClient.postInvoicesInvoiceIdToUpdate(invoiceUpdate, "application/json", invoice.getId(), "paid");
                    } catch (Exception e) {
                        container.increaseNumberOfErrors();
                    }
                    payment.setVoucherNumber(indiviudalResponses.get(0).getVoucherNumber());
                    payment.setVoucherAmount(String.valueOf(totalAmount));
                    payment.setVoucherCurrency(new PaymentVoucherCurrency().value(indiviudalResponses.get(0).getCurrency()));
                    payment.setPaymentStatus(new PaymentPaymentStatus().value("PAID").desc("bezahlt"));
                }
            }
        }
        return container;
    }

    /**
     * filters a list of Invoices according a given voucher date
     * @param date the date of the voucher date to be returned
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
}

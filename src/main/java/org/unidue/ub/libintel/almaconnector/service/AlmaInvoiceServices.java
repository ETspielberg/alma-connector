package org.unidue.ub.libintel.almaconnector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.acq.*;
import org.unidue.ub.libintel.almaconnector.clients.acquisition.AlmaInvoicesApiClient;
import org.unidue.ub.libintel.almaconnector.model.run.AlmaExportRun;
import org.unidue.ub.libintel.almaconnector.repository.AlmaExportRunRepository;

import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class AlmaInvoiceServices {

    private final AlmaInvoicesApiClient almaInvoicesApiClient;

    private final AlmaExportRunRepository almaExportRunRepository;

    private final static Logger log = LoggerFactory.getLogger(AlmaInvoiceServices.class);

    /**
     * constructor based autowiring of the Feign client and the AlmaExportRun repository
     *
     * @param almaInvoicesApiClient the Feign client for the Alma Invoice API
     */
    AlmaInvoiceServices(AlmaInvoicesApiClient almaInvoicesApiClient,
                        AlmaExportRunRepository almaExportRunRepository) {
        this.almaInvoicesApiClient = almaInvoicesApiClient;
        this.almaExportRunRepository = almaExportRunRepository;
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

package org.unidue.ub.libintel.almaconnector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.acq.Invoice;
import org.unidue.ub.alma.shared.acq.Invoices;
import org.unidue.ub.libintel.almaconnector.clients.acquisition.AlmaInvoicesApiClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class AlmaInvoiceServices {

    private final AlmaInvoicesApiClient almaInvoicesApiClient;

    private final static Logger log = LoggerFactory.getLogger(AlmaInvoiceServices.class);

    /**
     * constructor based autowiring of the Feign client
     * @param almaInvoicesApiClient the Feign client for the Alma Invoice API
     */
    AlmaInvoiceServices(AlmaInvoicesApiClient almaInvoicesApiClient) {
        this.almaInvoicesApiClient = almaInvoicesApiClient;
    }

    /**
     * retrieves the open invoices from the Alma API.
     * @return  a list of invoices
     */
    public List<Invoice> getOpenInvoices() {
        // initialize parameters
        int batchSize = 100;
        int offset = 0;

        // retrieve first list of po-lines.
        Invoices invoices = this.almaInvoicesApiClient.getInvoices("application/json", "ACTIVE", "", "", "", "", batchSize, offset, "");
        List<Invoice> invoiceList = new ArrayList<>(invoices.getInvoice());

        log.info("retrieving " + invoices.getTotalRecordCount() + " invoices");

        // as long as not all data are being collected, collect further
        while (invoiceList.size() < invoices.getTotalRecordCount()) {
            offset += batchSize;
            invoices = this.almaInvoicesApiClient.getInvoices("application/json", "ACTIVE", "", "", "", "", batchSize, offset, "");
            invoiceList.addAll(invoices.getInvoice());
        }
        return invoiceList;
    }
}

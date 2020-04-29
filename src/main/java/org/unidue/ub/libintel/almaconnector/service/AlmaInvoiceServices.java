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

    AlmaInvoiceServices(AlmaInvoicesApiClient almaInvoicesApiClient) {
        this.almaInvoicesApiClient = almaInvoicesApiClient;
    }

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
            invoices = this.almaInvoicesApiClient.getInvoices("application/json", "ACTIVE", "", "", "", "", batchSize, offset, "");;
            invoiceList.addAll(invoices.getInvoice());
        }
        return invoiceList;
    }

    private void addIfStatus(Invoices invoices, String status, List<Invoice> invoiceList) {
        invoices.getInvoice().forEach(
                entry -> {
                    if (entry.getInvoiceStatus().getValue().equals(status))
                        invoiceList.add(entry);}
        );
    }
}

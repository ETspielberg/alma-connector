package org.unidue.ub.libintel.almaconnector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.acq.*;
import org.unidue.ub.libintel.almaconnector.clients.analytics.AlmaAnalyticsReportClient;
import org.unidue.ub.libintel.almaconnector.model.InvoiceUpdate;
import org.unidue.ub.libintel.almaconnector.model.SapData;
import org.unidue.ub.libintel.almaconnector.model.SapResponse;
import org.unidue.ub.libintel.almaconnector.model.analytics.InvoiceForPayment;
import org.unidue.ub.libintel.almaconnector.model.analytics.InvoiceForPaymentReport;
import org.unidue.ub.libintel.almaconnector.model.run.AlmaExportRun;
import org.unidue.ub.libintel.almaconnector.model.run.SapResponseRun;
import org.unidue.ub.libintel.almaconnector.repository.AlmaExportRunRepository;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaInvoiceServices;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaPoLineService;

import java.io.IOException;
import java.util.*;

import static org.unidue.ub.libintel.almaconnector.Utils.convertToSapData;

@Service
@Secured({"ROLE_SYSTEM", "ROLE_SAP", "ROLE_ALMA_Invoice Operator Extended"})
public class SapService {

    private final AlmaInvoiceServices almaInvoiceServices;

    private final AlmaExportRunRepository almaExportRunRepository;

    private final AlmaPoLineService almaPoLineService;

    private final AlmaAnalyticsReportClient almaAnalyticsReportClient;

    private final Logger log = LoggerFactory.getLogger(SapService.class);

    @Value("${sap.home.tax.keys}")
    private List<String> homeTaxKeys;

    SapService(AlmaInvoiceServices almaInvoiceServices,
               AlmaPoLineService almaPoLineService,
               AlmaExportRunRepository almaExportRunRepository,
               AlmaAnalyticsReportClient almaAnalyticsReportClient) {
        this.almaInvoiceServices = almaInvoiceServices;
        this.almaPoLineService = almaPoLineService;
        this.almaExportRunRepository = almaExportRunRepository;
        this.almaAnalyticsReportClient = almaAnalyticsReportClient;
    }


    public List<Invoice> getOpenInvoicesFromAnalytics(AlmaExportRun almaExportRun) {
        try {
            List<InvoiceForPayment> result = this.almaAnalyticsReportClient.getReport(InvoiceForPayment.PATH, InvoiceForPaymentReport.class).getRows();
            List<Invoice> invoices = new ArrayList<>();
            if (result != null) {
                for (InvoiceForPayment invoiceEntry: result) {
                    if (invoiceEntry.getInvoiceOwnerCode().equals(almaExportRun.getInvoiceOwner())) {
                        Invoice invoiceInd = this.almaInvoiceServices.retrieveInvoice(invoiceEntry.getInvoiceNumber());
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
     * collects the invoices from alma and saves them to the alma export run object
     *
     * @param almaExportRun the alma export run object containing the data for the invoices to collect
     * @return the alma export run object holding the list of invoices
     */
    public AlmaExportRun getInvoices(AlmaExportRun almaExportRun) {
        List<Invoice> invoices;
        if (almaExportRun.isDateSpecific()) {
            log.info("collecting invoices for date");
            invoices = almaInvoiceServices.getOpenInvoicesForDate(almaExportRun.getDesiredDate(), almaExportRun.getInvoiceOwner());
        } else {
            log.info("collecting all invoices");
            invoices = almaInvoiceServices.getOpenInvoices(almaExportRun.getInvoiceOwner());
        }
        log.info("retrieved " + invoices.size() + " (filtered) invoices");
        almaExportRun.setInvoices(invoices);
        almaExportRun.setLastRun(new Date());
        this.almaExportRunRepository.save(almaExportRun);
        return almaExportRun;
    }

    /**
     * updates the Invoices in Alma with the results of the SAP import
     *
     * @param container an SAP container object holding a list of SAP response object
     * @return the SAP container objects the number of missed entries
     */
    public SapResponseRun updateInvoiceWithErpData(SapResponseRun container) {
        HashMap<String, Double> poLines = new HashMap<>();
        for (SapResponse sapResponse : container.getResponses()) {
            // get the number of the invoice (is not the ID!)
            String invoiceId = sapResponse.getInvoiceNumber();

            // search the invoices for the invoice number
            Invoices invoices = almaInvoiceServices.getInvoicesForInvocieId(invoiceId);
            // process only if there is only one invoice found. Otherwise increase number of errors and log the error
            if (invoices.getTotalRecordCount() == 1) {

                Invoice invoice = invoices.getInvoice().get(0);
                log.info("processing SAP responce for Invoice " + invoiceId);
                for (InvoiceLine invoiceLine: invoice.getInvoiceLines().getInvoiceLine())
                    if (poLines.containsKey(invoiceLine.getPoLine())) {
                        Double sum = poLines.get(invoiceLine.getPoLine());
                         sum += invoiceLine.getPrice();
                         poLines.put(invoiceLine.getPoLine(), sum);
                    } else
                        poLines.put(invoiceLine.getPoLine(), invoiceLine.getPrice());
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
                        almaInvoiceServices.addFullPayment(invoice, payment);

                        // otherwise just add the payment
                    } else {
                        almaInvoiceServices.addPartialPayment(invoice, payment);
                        InvoiceUpdate invoiceUpdate = new InvoiceUpdate(payment);
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
        checkAndClosePoLines(poLines);
        log.info(container.logString());
        return container;
    }

    private void checkAndClosePoLines(HashMap<String, Double> poLines) {
        poLines.forEach(
                (entry, sum) -> {
                    PoLine poLine = this.almaPoLineService.getPoLine(entry);
                    try {
                        double price = Double.parseDouble(poLine.getPrice().getSum());
                        if (price == sum) {
                            boolean success = this.almaPoLineService.closePoLine(poLine);
                            if (!success)
                                log.warn(String.format("could not close po line %s", entry));
                        }
                    } catch (Exception exception) {
                        log.warn(String.format("could not parse price for po line %s", entry));
                    }
                }
        );
    }
}

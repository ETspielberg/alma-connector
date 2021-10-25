package org.unidue.ub.libintel.almaconnector.service.alma;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.acq.*;
import org.unidue.ub.libintel.almaconnector.clients.alma.acquisition.AlmaInvoicesApiClient;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrder;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrderLine;
import org.unidue.ub.libintel.almaconnector.model.sap.InvoiceUpdate;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * offers functions around invoices in Alma
 *
 * @author Eike Spielberg
 * @author eike.spielberg@uni-due.de
 * @version 1.0
 */
@Service
@Slf4j
public class AlmaInvoiceService {

    private final AlmaInvoicesApiClient almaInvoicesApiClient;

    /**
     * constructor based autowiring of the Feign client
     *
     * @param almaInvoicesApiClient the Feign client for the Alma Invoice API
     */
    AlmaInvoiceService(AlmaInvoicesApiClient almaInvoicesApiClient) {
        this.almaInvoicesApiClient = almaInvoicesApiClient;
    }



    /**
     * retrieves the open invoices from the Alma API.
     *
     * @return a list of invoices
     */
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
    public List<Invoice> getOpenInvoicesForDate(Date date, String owner) {
        log.info("collecting invoices for date " + new SimpleDateFormat("dd.MM.yyyy").format(date));
        return filterList(date, getOpenInvoices(owner));
    }

    /**
     * saves a new invoice to Alma
     * @param invoice the invoice to be saved
     * @return the saved invoice
     */
    public Invoice saveInvoice(Invoice invoice) {
        return this.almaInvoicesApiClient.postAcqInvoices(invoice, "application/json");
    }

    /**
     * updates an invoice in Alma
     * @param invoice the invoice to be updated
     * @return the saved invoice
     */
    public Invoice updateInvoice(Invoice invoice) {
        return this.almaInvoicesApiClient.putInvoicesInvoiceId(invoice, "application/json", invoice.getId());
    }

    /**
     * retrieved an invoice by the invoice id
     * @param invoiceNumber the invoice id
     * @return the invoice
     */
    public Invoice retrieveInvoice(String invoiceNumber) {
        return this.almaInvoicesApiClient.getInvoicesInvoiceId("application/json", invoiceNumber, "full");
    }

    /**
     * retrieves an invoice by the invoice number
     * @param invoiceNumber the invoice number
     * @return the an <class>Invoices</class> object, holding all invoices with that invoice number
     */
    public Invoices getInvoicesForInvocieId(String invoiceNumber) {
        String searchQuery = "invoice_number~" + invoiceNumber;
        Invoices invoices = this.almaInvoicesApiClient.getInvoices("application/json", "ACTIVE",
                "Waiting to be Sent", "", "", searchQuery, 20, 0, "");

        log.debug(String.format("found %d invoices for invoice number %s", invoices.getTotalRecordCount(), invoiceNumber));
        return invoices;
    }

    /**
     * adds payment information for a partial payment
     * @param invoice the invoice to be updated
     * @param payment the information for the partial payment
     */
    public void addPartialPayment(Invoice invoice, Payment payment) {
        InvoiceUpdate invoiceUpdate = new InvoiceUpdate(payment);
        this.almaInvoicesApiClient.postInvoicesInvoiceIdToUpdate(invoiceUpdate, "application/json", invoice.getId(), "paid");
    }

    /**
     * adds payment information for a full payment
     * @param invoice the invoice to be updated
     * @param payment the information for the full payment
     */
    public void addFullPayment(Invoice invoice, Payment payment) {
        payment.setPaymentStatus(new PaymentPaymentStatus().value("PAID").desc("bezahlt"));
        InvoiceUpdate invoiceUpdate = new InvoiceUpdate(payment);
        this.almaInvoicesApiClient.postInvoicesInvoiceIdToUpdate(invoiceUpdate, "application/json", invoice.getId(), "paid");
    }

    /**
     * saves an invoice line in alma
     * @param id the id of the invoice
     * @param invoiceLine the invoice line to be added
     */
    public void addInvoiceLine(String id, InvoiceLine invoiceLine) {
        this.almaInvoicesApiClient.postInvoicesInvoiceIdLines(invoiceLine, "application/json", id);
    }

    /**
     * process an invoice
     * @param id the id of the invoice to be processed
     */
    public void processInvoice(String id) {
        this.almaInvoicesApiClient.postInvoicesInvoiceId(new Invoice(), "application/json", id, "process_invoice");
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

    /**
     * creates an invoice for a bubi order.
     *
     * @param bubiOrder a bubi order
     * @return an Alma Invoice object
     */
    public Invoice getInvoiceForBubiOrder(BubiOrder bubiOrder) {
        // create new Invocie
        Invoice invoice = new Invoice();

        // set the vendor information with the information from the bubi order
        invoice.vendor(new InvoiceVendor().value(bubiOrder.getVendorAccount()))
                .vendorAccount(bubiOrder.getVendorAccount());

        // set total amount and payment method
        invoice.totalAmount(bubiOrder.getTotalAmount());
        invoice.paymentMethod(new InvoicePaymentMethod().value("ACCOUNTINGDEPARTMENT"));

        // set the status information
        invoice.invoiceStatus(new InvoiceInvoiceStatus().value("ACTIVE"));


        // set the VAT information
        invoice.invoiceVat(new InvoiceVat().vatPerInvoiceLine(true).type(new InvoiceVatType().value("INCLUSIVE")));

        // set the invoice number and date
        invoice.setNumber(bubiOrder.getInvoiceNumber());
        invoice.setInvoiceDate(bubiOrder.getInvoiceDate());

        // set the owner of the order line
        Optional<BubiOrderLine> option = bubiOrder.getBubiOrderLines().stream().findFirst();
        if (option.isPresent()) {

            if (option.get().getCollection().startsWith("D"))
            invoice.setOwner(new InvoiceOwner().value("D0001"));
        else
            invoice.setOwner(new InvoiceOwner().value("E0001"));
        } else
            invoice.setOwner(new InvoiceOwner().value("E0001"));
        return invoice;
    }

    /**
     * creates the individual invoice lines for the bubi order lines in the bubi order
     *
     * @param bubiOrder the bubi order holding the individual bubi order lines
     * @return a list of Alma InvoiceLine-objects
     */
    public List<InvoiceLine> getInvoiceLinesForBubiOrder(BubiOrder bubiOrder, boolean distribute) {
        // create new list of order lines
        List<InvoiceLine> invoiceLines = new ArrayList<>();
        bubiOrder.getBubiOrderLines().forEach(bubiOrderLine -> invoiceLines.add(this.createInvoiceLine(bubiOrderLine)));
        if (!distribute) {
            InvoiceLineVat invoiceLineVat = new InvoiceLineVat().vatCode(new InvoiceLineVatVatCode().value("H8"));

            // set the fund distribution
            FundDistributionFundCode fundDistributionFundCode = new FundDistributionFundCode().value(bubiOrder.getAdditionalCostsFund());
            FundDistribution fundDistribution = new FundDistribution().fundCode(fundDistributionFundCode).amount(bubiOrder.getAdditionalCosts());
            List<FundDistribution> fundDistributionList = new ArrayList<>();
            fundDistributionList.add(fundDistribution);

            // create invoice line with all information and add it to the list
            invoiceLines.add(new InvoiceLine()
                    .fullyInvoiced(true)
                    .totalPrice(bubiOrder.getAdditionalCosts())
                    .invoiceLineVat(invoiceLineVat)
                    .fundDistribution(fundDistributionList));
        }

        return invoiceLines;
    }

    private InvoiceLine createInvoiceLine(BubiOrderLine bubiOrderLine) {
        InvoiceLineVat invoiceLineVat = new InvoiceLineVat().vatCode(new InvoiceLineVatVatCode().value("H8"));

        // set the fund distribution
        FundDistributionFundCode fundDistributionFundCode = new FundDistributionFundCode().value(bubiOrderLine.getFund());
        FundDistribution fundDistribution = new FundDistribution().fundCode(fundDistributionFundCode).amount(bubiOrderLine.getPrice());
        List<FundDistribution> fundDistributionList = new ArrayList<>();
        fundDistributionList.add(fundDistribution);

        // create invoice line with all information and add it to the list
        return new InvoiceLine()
                .poLine(bubiOrderLine.getAlmaPoLineId())
                .fullyInvoiced(true)
                .totalPrice(bubiOrderLine.getPrice())
                .invoiceLineVat(invoiceLineVat)
                .fundDistribution(fundDistributionList);
    }

    public Invoice buildInvoiceForBubiOrder(BubiOrder bubiOrder, boolean distribute) {
        Invoice invoice = this.getInvoiceForBubiOrder(bubiOrder);
        invoice = this.saveInvoice(invoice);
        List<InvoiceLine> invoiceLines = this.getInvoiceLinesForBubiOrder(bubiOrder, distribute);
        for (InvoiceLine invoiceLine : invoiceLines)
            this.addInvoiceLine(invoice.getId(), invoiceLine);
        this.processInvoice(invoice.getId());
        return invoice;
    }

    public List<Invoice> getEdiInvoices(String vendorId) {
        int limit = 100;
        int offset = 0;
        Invoices inovices = this.almaInvoicesApiClient.getInvoices("application/json",
                "ACTIVE",
                "InReview",
                "", "EDI",
                "vendor_code~" + vendorId,
                limit,
                offset,
                "");
        List<Invoice> allInvoices = new ArrayList<>(inovices.getInvoice());
        while (allInvoices.size() < inovices.getTotalRecordCount()) {
            offset += limit;
            Invoices additionalInovices = this.almaInvoicesApiClient.getInvoices("application/json",
                    "ACTIVE",
                    "InReview",
                    "", "EDI",
                    "vendor_code~" + vendorId,
                    limit,
                    offset,
                    "");
            allInvoices.addAll(additionalInovices.getInvoice());
        }
        return allInvoices;
    }

    public void updateEdiInvoices(String vendorId) {
        List<Invoice> invoices = this.getEdiInvoices(vendorId);
        for (Invoice invoice : invoices) {
            invoice = this.almaInvoicesApiClient.getInvoicesInvoiceId("application/json", invoice.getId(), "");
            String vatCode = invoice.getInvoiceVat().getVatCode().getValue();
            double vatAmount = invoice.getInvoiceVat().getVatAmount();

            invoice.getInvoiceVat().setVatPerInvoiceLine(true);
            for (InvoiceLine invoiceLine : invoice.getInvoiceLines().getInvoiceLine()) {
                invoiceLine.setPriceNote(invoiceLine.getNote());
                invoiceLine.setNote("");
                invoiceLine.getInvoiceLineVat().getVatCode().setValue(vatCode);
                invoiceLine.getInvoiceLineVat().setVatAmount(vatAmount);
                try {
                    this.updateInvoiceLine(invoice.getId(), invoiceLine);
                } catch (Exception e) {
                    log.warn("could not update invoice " + invoice.getId(), e);
                }
            }
            this.updateInvoice(invoice);
        }
    }

    private void updateInvoiceLine(String invoiceId, InvoiceLine invoiceLine) {
        this.almaInvoicesApiClient.putInvoicesInvoiceIdLinesInvoiceLineId(invoiceLine, "application/json", invoiceId,invoiceLine.getId());
    }
}

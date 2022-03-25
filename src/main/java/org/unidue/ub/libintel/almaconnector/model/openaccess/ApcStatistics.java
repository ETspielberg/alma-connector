package org.unidue.ub.libintel.almaconnector.model.openaccess;

import org.unidue.ub.libintel.almaconnector.model.analytics.ApcData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "apc_statistics")
public class ApcStatistics {

    @Column(name = "mms_id")
    @Id
    private String mmsid;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "vendor_code")
    private String vendorCode;

    @Column(name = "vendor")
    private String vendor;

    @Column(name = "creditor")
    private String creditor;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

    @Column(name = "ledger")
    private String ledger;

    @Column(name = "invoice_line_note")
    private String invoiceLineNote;

    @Column(name = "invoice_price_note")
    private String invoicePriceNote;

    @Column(name = "invoice_vat_code")
    private String invoiceVatCode;

    @Column(name = "invoice_vat_percent")
    private double invoiceVatPercent;

    @Column(name = "first_author")
    private String firstAuthor;

    @Column(name = "faculty")
    private String faculty;

    @Column(name = "currency")
    private String currency;

    @Column(name = "list_price")
    private double listPrice;

    @Column(name = "netto_costs")
    private double nettoCosts;

    @Column(name = "tax_costs")
    private double taxCosts;

    @Column(name = "bank_costs")
    private double bankCosts;

    @Column(name = "payment")
    private double payment;

    @Column(name = "voucher_number")
    private String voucherNumber;

    @Column(name = "real_total_costs")
    private double realTotalCosts;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "journal")
    private String journal;

    @Column(name = "issn")
    private String issn;

    @Column(name = "doi")
    private String doi;

    @Column(name = "publishing_date")
    private String publishingDate;

    @Column(name = "non_ca_authors")
    private String nonCaAuthors;

    @Column(name = "title")
    private String title;

    @Column(name = "duepublico_id")
    private String duepublicoId;

    @Column(name = "comment")
    private String comment;

    @Column(name = "year")
    private int year;

    @Column(name = "institute")
    private String institute;

    @Column(name = "is_finished")
    private boolean isFinished;

    public ApcStatistics() {
    }

    public ApcStatistics(ApcData apcData) {
        this.mmsid = apcData.getMmsId();
        this.ledger = apcData.getLedger();
        this.currency = apcData.getCurrency();
        this.invoiceDate = apcData.getInvoiceDate();
        this.invoiceNumber = apcData.getInvoiceNumber();
        this.voucherNumber = apcData.getVoucherNumber();
        this.orderNumber = apcData.getOrderlineNumber();
        this.vendor = apcData.getVendorName();
        this.vendorCode = apcData.getVendorCode();
        this.invoicePriceNote = apcData.getInvoiceLinePriceNote();
        this.invoiceLineNote = apcData.getInvoiceLineNote();
        this.currency = apcData.getCurrency();
        this.invoiceVatCode = apcData.getVatCode();
        this.payment = apcData.getTransactionAmount();
        this.taxCosts = apcData.getVatNoteData();
        this.nettoCosts = apcData.getTotalPrice() - apcData.getVatNoteData();
        this.realTotalCosts = apcData.getTotalPrice();
        this.creditor = apcData.getCreditor();
        this.creationDate = apcData.getInvoiceCreationDate();
        this.invoiceVatPercent = apcData.getVatPercent();
        this.isFinished = false;
        this.listPrice = apcData.getListPrice();
        this.bankCosts = 0.0;
    }

    public String getMmsid() {
        return mmsid;
    }

    public void setMmsid(String mmsid) {
        this.mmsid = mmsid;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getCreditor() {
        return creditor;
    }

    public void setCreditor(String creditor) {
        this.creditor = creditor;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getFirstAuthor() {
        return firstAuthor;
    }

    public void setFirstAuthor(String firstAuthor) {
        this.firstAuthor = firstAuthor;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public double getNettoCosts() {
        return nettoCosts;
    }

    public void setNettoCosts(double nettoCosts) {
        this.nettoCosts = nettoCosts;
    }

    public double getTaxCosts() {
        return taxCosts;
    }

    public void setTaxCosts(double taxCosts) {
        this.taxCosts = taxCosts;
    }

    public double getBankCosts() {
        return bankCosts;
    }

    public void setBankCosts(double bankCosts) {
        this.bankCosts = bankCosts;
    }

    public double getPayment() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public String getVoucherNumber() {
        return voucherNumber;
    }

    public void setVoucherNumber(String voucherNumber) {
        this.voucherNumber = voucherNumber;
    }

    public double getRealTotalCosts() {
        return realTotalCosts;
    }

    public void setRealTotalCosts(double realTotalCosts) {
        this.realTotalCosts = realTotalCosts;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getPublishingDate() {
        return publishingDate;
    }

    public void setPublishingDate(String publishingDate) {
        this.publishingDate = publishingDate;
    }

    public String getNonCaAuthors() {
        return nonCaAuthors;
    }

    public void setNonCaAuthors(String nonCaAuthors) {
        this.nonCaAuthors = nonCaAuthors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuepublicoId() {
        return duepublicoId;
    }

    public void setDuepublicoId(String duepublicoId) {
        this.duepublicoId = duepublicoId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public String getLedger() {
        return ledger;
    }

    public void setLedger(String ledger) {
        this.ledger = ledger;
    }

    public String getInvoiceLineNote() {
        return invoiceLineNote;
    }

    public void setInvoiceLineNote(String invoiceLineNote) {
        this.invoiceLineNote = invoiceLineNote;
    }

    public String getInvoicePriceNote() {
        return invoicePriceNote;
    }

    public void setInvoicePriceNote(String invoicePriceNote) {
        this.invoicePriceNote = invoicePriceNote;
    }

    public String getInvoiceVatCode() {
        return invoiceVatCode;
    }

    public void setInvoiceVatCode(String invoiceVatCode) {
        this.invoiceVatCode = invoiceVatCode;
    }

    public double getInvoiceVatPercent() {
        return invoiceVatPercent;
    }

    public void setInvoiceVatPercent(double invoiceVatPercent) {
        this.invoiceVatPercent = invoiceVatPercent;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getInstitute() {
        return institute;
    }

    public boolean getIsFinished() {
        return isFinished;
    }

    public void setIsFinished(boolean finished) {
        isFinished = finished;
    }

    public double getListPrice() {
        return listPrice;
    }

    public void setListPrice(double listPrice) {
        this.listPrice = listPrice;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
        if (institute.toLowerCase().contains("medizin") || institute.contains("klinik"))
            this.faculty = "Medizin";
        else if (institute.toLowerCase().contains("biologie"))
            this.faculty = "Biologie";
        else if (institute.toLowerCase().contains("physik"))
            this.faculty = "Physik";
        else if (institute.toLowerCase().contains("mathemat"))
            this.faculty = "Mathematik";
        else if (institute.toLowerCase().contains("geisteswissen"))
            this.faculty = "Geisteswissenschaften";
        else if (institute.toLowerCase().contains("wirtschaftswissen"))
            this.faculty = "Wirtschaftswissenschaften";
        else if (institute.toLowerCase().contains("mercator"))
            this.faculty = "Mercator School of Management";
        else if (institute.contains("Bildungswissen"))
            this.faculty = "Bildungswissenschaften";
        else if (institute.toLowerCase().contains("chemi"))
            this.faculty = "Chemie";
        else if (institute.toLowerCase().contains("gesellschaftswissenschaften"))
            this.faculty = "Gesellschaftswissenschaften";
        else if (institute.toLowerCase().contains("ingenieur"))
            this.faculty = "Ingenieurwissenschaften";
        else
            this.faculty = institute;
    }

    public void update(JournalApcDataDto journalApcDataDto) {
        this.comment = journalApcDataDto.getNote();
        this.duepublicoId = journalApcDataDto.getDuepublicoId();
        this.doi = journalApcDataDto.getDoi();
    }

    public void addComment(String comment) {
        if (this.comment == null || this.comment.isEmpty())
            this.comment = comment;
        else
            this.comment += ";\n" + comment;
    }

    public void addAuthor(String author) {
        if (this.nonCaAuthors == null || this.nonCaAuthors.isEmpty())
            this.nonCaAuthors = author;
        else
            this.nonCaAuthors += "; " + author;
    }

    public void addSubTitle(String subTitle) {
        this.title += ". " + subTitle;
    }

    public void update(ApcData apcData) {
        if (apcData.getInvoiceNumber().endsWith("_Bank")) {
            this.bankCosts = apcData.getTransactionAmount();
            this.realTotalCosts = this.nettoCosts + this.taxCosts + this.bankCosts;
            this.isFinished = true;
        } else {
            this.ledger = apcData.getLedger();
            this.currency = apcData.getCurrency();
            this.invoiceDate = apcData.getInvoiceDate();
            this.invoiceNumber = apcData.getInvoiceNumber();
            this.voucherNumber = apcData.getVoucherNumber();
            this.orderNumber = apcData.getOrderlineNumber();
            this.vendor = apcData.getVendorName();
            this.vendorCode = apcData.getVendorCode();
            this.invoicePriceNote = apcData.getInvoiceLinePriceNote();
            this.invoiceLineNote = apcData.getInvoiceLineNote();
            this.currency = apcData.getCurrency();
            this.invoiceVatCode = apcData.getVatCode();
            this.payment = apcData.getTransactionAmount();
            this.taxCosts = apcData.getVatNoteData();
            this.nettoCosts = apcData.getTotalPrice() - apcData.getVatNoteData();
            this.realTotalCosts = apcData.getTotalPrice();
            this.creditor = apcData.getCreditor();
            this.creationDate = apcData.getInvoiceCreationDate();
            this.invoiceVatPercent = apcData.getVatPercent();
            if ("EUR".equals(currency) || isFinished)
                this.isFinished = true;
            else {
                this.isFinished = "Yes".equals(apcData.getExplicitRatio());
            }
        }
    }

    @Override
    public String toString() {
        return "class: 'ApcStatistics'; " +
                "mmsId: '" + mmsid + "'; " +
                "creationDate: '" + creationDate + "'; " +
                "vendorCode: '" + vendorCode + "'; " +
                "vendor: '" + vendor + "'; " +
                "creditor: '" + creditor + "'; " +
                "orderNumber: '" + orderNumber + "'; " +
                "invoiceNumber: '" + invoiceNumber + "'; " +
                "invoiceDate: '" + invoiceDate + "'; " +
                "ledger: '" + ledger + "'; " +
                "invoiceLineNote: '" + invoiceLineNote + "'; " +
                "invoiceVatCode: '" + invoiceVatCode + "'; " +
                "invoiceVatPercent: '" + invoiceVatPercent + "'; " +
                "firstAuthor: '" + firstAuthor + "'; " +
                "faculty: '" + faculty + "'; " +
                "institute: '" + institute + "'; " +
                "currency: '" + currency + "'; " +
                "nettoCosts: '" + nettoCosts + "'; " +
                "taxCosts: '" + taxCosts + "'; " +
                "bankCosts: '" + bankCosts + "'; " +
                "payment: '" + payment + "'; " +
                "voucherNumber: '" + voucherNumber + "'; " +
                "realTotalCosts: '" + realTotalCosts + "'; " +
                "journal: '" + journal + "'; " +
                "issn: '" + issn + "'; " +
                "doi: '" + doi + "'; " +
                "publishingDate: '" + publishingDate + "'; " +
                "nonCaAuthors: '" + nonCaAuthors + "'; " +
                "title: '" + title + "'; " +
                "duepublicoId: '" + duepublicoId + "'; " +
                "comment: '" + comment + "'; " +
                "year: '" + year;
    }
}

package org.unidue.ub.libintel.almaconnector.model.openaccess;

import lombok.Data;

import java.time.LocalDate;

@Data
public class JournalApcDataDto {

    private String mmsId;
    private String title;
    private String correspondingAuthor;
    private String faculty;
    private String authors;
    private LocalDate creationDate;
    private String vendor;
    private String invoiceNumber;
    private double invoiceAmount;
    private LocalDate invoiceDate;
    private String currency;
    private double invoiceVatPercent;
    private double nettoCosts;
    private double taxCosts;
    private double bankCosts;
    private double totalCosts;
    private String journal;
    private String issn;
    private String publisher;
    private String doi;
    private String publishingDate;
    private String duepublicoId;
    private int year;
    private String orderNumber;
    private String voucherNumber;
    private String note;
    private boolean isFinished;

    public JournalApcDataDto() {}

    public JournalApcDataDto(ApcStatistics apcStatistics) {
        this.mmsId = apcStatistics.getMmsid();
        this.title = apcStatistics.getTitle();
        this.correspondingAuthor = apcStatistics.getFirstAuthor();
        this.faculty = apcStatistics.getFaculty();
        this.creationDate = apcStatistics.getCreationDate();
        this.vendor = apcStatistics.getVendor();
        this.invoiceNumber = apcStatistics.getInvoiceNumber();
        this.invoiceAmount = apcStatistics.getListPrice();
        this.invoiceDate = apcStatistics.getInvoiceDate();
        this.currency = apcStatistics.getCurrency();
        this.invoiceVatPercent = apcStatistics.getInvoiceVatPercent();
        this.totalCosts = apcStatistics.getRealTotalCosts();
        this.journal = apcStatistics.getJournal();
        this.issn = apcStatistics.getIssn();
        this.publisher = apcStatistics.getPublisher();
        this.doi = apcStatistics.getDoi();
        this.publishingDate = apcStatistics.getPublishingDate();
        this.duepublicoId = apcStatistics.getDuepublicoId();
        this.year = apcStatistics.getYear();
        this.orderNumber = apcStatistics.getOrderNumber();
        this.voucherNumber = apcStatistics.getVoucherNumber();
        this.note = apcStatistics.getComment();
        this.isFinished = apcStatistics.getIsFinished();
        this.taxCosts = apcStatistics.getTaxCosts();
        this.bankCosts = apcStatistics.getBankCosts();
        this.nettoCosts = apcStatistics.getNettoCosts();
    }
}

package org.unidue.ub.libintel.almaconnector.model;

import org.unidue.ub.alma.shared.acq.Invoice;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="alma_export_run")
public class AlmaExportRun {

    @Id
    private String identifier;

    @Column(name="date_specific")
    private boolean dateSpecific = false;

    @Column(name="desired_date")
    private Date desiredDate;

    @Column(name="last_run")
    private Date lastRun;

    @Column(name="run_index")
    private long runIndex;

    @Column(name="files_created")
    private List<String> filesCreated = new ArrayList<>();

    @Column(name="number_invoices")
    private long numberInvoices = 0;

    @Column(name="number_invoice_lines")
    private long numberInvoiceLines = 0;

    @Column(name="successfull_sap_data")
    private long successfullSapData = 0;

    @Column(name="missed_sap_data")
    private long missedSapData = 0;

    @Column(name="missed_invoice_lines")
    private List<String> missedInvoiceLines = new ArrayList<>();

    @Column(name="empty_invoices")
    private List<String> emptyInvoices = new ArrayList<>();

    @Transient
    private List<SapData> missedSapDataList = new ArrayList<>();

    @Column(name="status")
    private String status;

    @Transient
    private List<Invoice> invoices= new ArrayList<>();

    @Transient
    private List<SapData> sapData= new ArrayList<>();

    public AlmaExportRun(String identifier) {
        this.identifier = identifier;
    }

    public AlmaExportRun withSpecificDate(Date desiredDate) {
        this.desiredDate = desiredDate;
        this.dateSpecific = true;
        return this;
    }

    public AlmaExportRun withRunIndex(long runIndex) {
        this.runIndex = runIndex;
        return this;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isDateSpecific() {
        return dateSpecific;
    }

    public void setDateSpecific(boolean dateSpecific) {
        this.dateSpecific = dateSpecific;
    }

    public Date getDesiredDate() {
        return desiredDate;
    }

    public void setDesiredDate(Date desiredDate) {
        this.desiredDate = desiredDate;
    }

    public Date getLastRun() {
        return lastRun;
    }

    public void setLastRun(Date lastRun) {
        this.lastRun = lastRun;
    }

    public List<String> getFilesCreated() {
        return filesCreated;
    }

    public void setFilesCreated(List<String> filesCreated) {
        this.filesCreated = filesCreated;
    }

    public long getNumberInvoices() {
        return numberInvoices;
    }

    public void setNumberInvoices(long numberInvoices) {
        this.numberInvoices = numberInvoices;
    }

    public long getNumberInvoiceLines() {
        return numberInvoiceLines;
    }

    public void setNumberInvoiceLines(long numberInvoiceLines) {
        this.numberInvoiceLines = numberInvoiceLines;
    }

    public long getSuccessfullSapData() {
        return successfullSapData;
    }

    public void setSuccessfullSapData(long successfullSapData) {
        this.successfullSapData = successfullSapData;
    }

    public long getMissedSapData() {
        return missedSapData;
    }

    public void setMissedSapData(long missedSapData) {
        this.missedSapData = missedSapData;
    }

    public void increaseMissedSapData() {
        this.missedSapData++;
    }

    public List<String> getMissedInvoiceLines() {
        return missedInvoiceLines;
    }

    public void setMissedInvoiceLines(List<String> missedInvoiceLines) {
        this.missedInvoiceLines = missedInvoiceLines;
    }

    public void increaseInvoiceLines(long numberInvoiceLines) {
        this.numberInvoiceLines += numberInvoiceLines;
    }

    public List<String> getEmptyInvoices() {
        return emptyInvoices;
    }

    public void setEmptyInvoices(List<String> emptyInvoices) {
        this.emptyInvoices = emptyInvoices;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public List<SapData> getSapData() {
        return sapData;
    }

    public void addMissedSapData(SapData sapData) {
        this.missedSapDataList.add(sapData);
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
        this.numberInvoices = invoices.size();
        for (Invoice invoice: this.invoices) {
            if (invoice.getInvoiceLines() != null && invoice.getInvoiceLines().getInvoiceLine() != null)
                this.numberInvoiceLines += invoice.getInvoiceLines().getInvoiceLine().size();
            else
                this.emptyInvoices.add(invoice.getId());
        }
    }

    public void setSapData(List<SapData> sapData) {
        this.sapData = sapData;
    }

    public void addSapData(SapData sapData) {
        this.sapData.add(sapData);
    }

    public void addSapDataList(List<SapData> sapDataList) {
        this.sapData.addAll(sapDataList);
    }

    public void sortSapData(){
        Collections.sort(this.sapData);
    }

    public List<SapData> getMissedSapDataList() {
        return missedSapDataList;
    }

    public void setMissedSapDataList(List<SapData> missedSapDataList) {
        this.missedSapDataList = missedSapDataList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getRunIndex() {
        return runIndex;
    }

    public void setRunIndex(long runIndex) {
        this.runIndex = runIndex;
    }

    public void increaseRunIndex() {
        this.runIndex++;
    }

    public void newRun() {
        this.sapData = new ArrayList<>();
        this.invoices = new ArrayList<>();
        this.filesCreated = new ArrayList<>();
        this.numberInvoiceLines = 0;
        this.numberInvoices = 0;
        this.successfullSapData = 0;
        this.missedSapData = 0;
    }
}

package org.unidue.ub.libintel.almaconnector.model.run;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.unidue.ub.alma.shared.acq.Invoice;
import org.unidue.ub.libintel.almaconnector.model.SapData;

import javax.persistence.*;
import java.util.*;

import static org.unidue.ub.libintel.almaconnector.Utils.dateformat;

@Entity
@Table(name="alma_export_run")
public class AlmaExportRun {

    @Id
    private String identifier;

    @Column(name="date_specific")
    private boolean dateSpecific = false;

    @Column(name="invoice_owner")
    private String invoiceOwner;

    @Column(name="desired_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date desiredDate;

    @Column(name="last_run")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date lastRun;

    @Column(name="run_index")
    private long runIndex;

    @Column(name="files_created")
    @ElementCollection
    private Set<String> filesCreated = new HashSet<>();

    @Column(name="number_invoices")
    private long numberInvoices = 0;

    @Column(name="number_invoice_lines")
    private long numberInvoiceLines = 0;

    @Column(name="successfull_sap_data")
    private long successfullSapData = 0;

    @Column(name="missed_sap_data")
    private long missedSapData = 0;

    @Column(name="number_home_sap_data")
    private long numberHomeSapData = 0;

    @Column(name="number_foreign_sap_data")
    private long numberForeignSapData = 0;

    @Column(name="missed_invoice_lines")
    @ElementCollection
    private Set<String> missedInvoiceLines = new HashSet<>();

    @Column(name="empty_invoices")
    @ElementCollection
    private Set<String> emptyInvoices = new HashSet<>();

    @Transient
    private List<SapData> missedSapDataList = new ArrayList<>();

    @Column(name="status")
    private String status;

    @Transient
    private List<Invoice> invoices= new ArrayList<>();

    @Transient
    private List<SapData> homeSapData = new ArrayList<>();

    @Transient
    private List<SapData> foreignSapData= new ArrayList<>();

    public AlmaExportRun() {
        this.desiredDate = new Date();
        this.runIndex = 0;
        this.invoiceOwner = "";
        this.identifier = String.format("%s - %s - %s", dateformat.format(desiredDate), invoiceOwner, this.runIndex);
    }

    public AlmaExportRun(Date desiredDate) {
        this.desiredDate = desiredDate;
        this.runIndex = 0;
        this.invoiceOwner = "";
        this.identifier = String.format("%s - %s - %s", dateformat.format(desiredDate), invoiceOwner, this.runIndex);
    }

    public AlmaExportRun withSpecificDate(Date desiredDate) {
        this.desiredDate = desiredDate;
        this.dateSpecific = true;
        return this;
    }

    public AlmaExportRun withInvoiceOwner(String invoiceOwner) {
        this.invoiceOwner = invoiceOwner;
        this.identifier = String.format("%s - %s - %s", dateformat.format(desiredDate), invoiceOwner, this.runIndex);
        return this;
    }

    public AlmaExportRun withRunIndex(long runIndex) {
        this.runIndex = runIndex;
        this.identifier = String.format("%s - %s - %s", dateformat.format(desiredDate), invoiceOwner, this.runIndex);
        return this;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getInvoiceOwner() {
        return invoiceOwner;
    }

    public void setInvoiceOwner(String invoiceOwner) {
        this.invoiceOwner = invoiceOwner;
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
        this.identifier = dateformat.format(desiredDate) + "-" + this.runIndex;
    }

    public Date getLastRun() {
        return this.lastRun;
    }

    public void setLastRun(Date lastRun) {
        this.lastRun = lastRun;
    }

    public Set<String> getFilesCreated() {
        return this.filesCreated;
    }

    public void setFilesCreated(Set<String> filesCreated) {
        this.filesCreated = filesCreated;
    }

    public long getNumberInvoices() {
        return this.numberInvoices;
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

    public void increaseSuccessfullSapData() { this.successfullSapData++; }

    public long getMissedSapData() {
        return this.missedSapData;
    }

    public void setMissedSapData(long missedSapData) {
        this.missedSapData = missedSapData;
    }

    public void increaseMissedSapData() {
        this.missedSapData++;
    }

    public Set<String> getMissedInvoiceLines() {
        return missedInvoiceLines;
    }

    public void setMissedInvoiceLines(Set<String> missedInvoiceLines) {
        this.missedInvoiceLines = missedInvoiceLines;
    }

    public void increaseInvoiceLines(long numberInvoiceLines) {
        this.numberInvoiceLines += numberInvoiceLines;
    }

    public Set<String> getEmptyInvoices() {
        return this.emptyInvoices;
    }

    public void setEmptyInvoices(Set<String> emptyInvoices) {
        this.emptyInvoices = emptyInvoices;
    }

    public List<Invoice> getInvoices() {
        return this.invoices;
    }

    public List<SapData> getHomeSapData() {
        return this.homeSapData;
    }

    public void addMissedSapData(SapData sapData) {
        this.missedSapDataList.add(sapData);
    }

    public long getNumberHomeSapData() {
        return this.numberHomeSapData;
    }

    public void setNumberHomeSapData(long numberHomeSapData) {
        this.numberHomeSapData = numberHomeSapData;
    }

    public void increaseNumberHomeSapData() {
        this.numberHomeSapData++;
    }

    public long getNumberForeignSapData() {
        return numberForeignSapData;
    }

    public void setNumberForeignSapData(long numberForeignSapData) {
        this.numberForeignSapData = numberForeignSapData;
    }

    public void increaseNumberForeignSapData() {
        this.numberForeignSapData++;
    }

    public List<SapData> getForeignSapData() {
        return this.foreignSapData;
    }

    public void setForeignSapData(List<SapData> foreignSapData) {
        this.foreignSapData = foreignSapData;
        this.numberForeignSapData = foreignSapData.size();
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
        this.invoices.removeIf(invoice -> !invoice.getOwner().getValue().equals(this.invoiceOwner));
        this.numberInvoices = invoices.size();
        for (Invoice invoice: this.invoices) {
            if (invoice.getInvoiceLines() != null && invoice.getInvoiceLines().getInvoiceLine() != null)
                this.numberInvoiceLines += invoice.getInvoiceLines().getInvoiceLine().size();
            else
                this.emptyInvoices.add(invoice.getId());
        }
    }

    public void setHomeSapData(List<SapData> homeSapData) {
        this.homeSapData = homeSapData;
        this.numberHomeSapData = homeSapData.size();
    }

    public void addSapData(SapData sapData, List<String> homeTaxKeys) {
        String taxKey;
        try {
            taxKey = sapData.costType.substring(0, 2);
        } catch (Exception e) {
            taxKey = "";
        }
        if (homeTaxKeys.contains(taxKey) && "EUR".equals(sapData.currency)) {
            this.homeSapData.add(sapData);
            this.numberHomeSapData++;
        } else {
            this.foreignSapData.add(sapData);
            this.numberForeignSapData++;
        }
    }

    public void addSapDataList(List<SapData> sapDataList, List<String> homeTaxKeys) {
        for (SapData sapData: sapDataList)
            addSapData(sapData, homeTaxKeys);
    }

    public void sortSapData(){
        Collections.sort(this.homeSapData);
        Collections.sort(this.foreignSapData);
    }

    public long getTotalSapData() {
        return this.homeSapData.size() + this.foreignSapData.size();
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
        this.homeSapData = new ArrayList<>();
        this.invoices = new ArrayList<>();
        this.filesCreated = new HashSet<>();
        this.numberInvoiceLines = 0;
        this.numberInvoices = 0;
        this.successfullSapData = 0;
        this.missedSapData = 0;
        this.numberForeignSapData = 0;
        this.numberHomeSapData = 0;
    }

    public long getNumberHomeDataSelected() {
        int number = 0;
        for (SapData sapData: this.homeSapData) {
            if (sapData.isChecked)
                number++;
        }
        return number;
    }

    public long getNumberForeignDataSelected() {
        int number = 0;
        for (SapData sapData: this.foreignSapData) {
            if (sapData.isChecked)
                number++;
        }
        return number;
    }

    public String log() {
        String logString = "runID: %s, date: %s, runIndex: %d, invoiceOwner %s, dateSpecific: %s, numberInvoices; %s, numberSapData: %s";
        return String.format(logString, this.identifier, this.desiredDate, this.runIndex, this.invoiceOwner, this.dateSpecific, this.invoices.size(),
                this.homeSapData.size());
    }

    public void updateIdentifier() {
        this.identifier = String.format("%s - %s - %s", dateformat.format(desiredDate), invoiceOwner, this.runIndex);
    }
}

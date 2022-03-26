package org.unidue.ub.libintel.almaconnector.model.run;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.keyvalue.annotation.KeySpace;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.format.annotation.DateTimeFormat;
import org.unidue.ub.alma.shared.acq.Invoice;
import org.unidue.ub.libintel.almaconnector.model.sap.AvailableInvoice;
import org.unidue.ub.libintel.almaconnector.model.sap.SapData;

import java.util.*;


/**
 * container object to hold all information about a download of the sap data from Alma
 */
@KeySpace("sapdata")
@RedisHash(value = "sapdata", timeToLive = 36000)
public class SapDataRun {

    @Id
    private String identifier;

    private String invoiceOwner;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date lastRun;

    private long runIndex;

    private Set<String> filesCreated = new HashSet<>();

    private long numberInvoices = 0;

    private long numberInvoiceLines = 0;

    private long successfullSapData = 0;

    private long missedSapData = 0;

    private long numberHomeSapData = 0;

    private long numberForeignSapData = 0;

    private Set<String> missedInvoiceLines = new HashSet<>();

    private Set<String> emptyInvoices = new HashSet<>();

    @Transient
    private List<SapData> missedSapDataList = new ArrayList<>();

    private String status;

    private List<AvailableInvoice> availableInvoices;

    private List<Invoice> invoices = new ArrayList<>();

    @Transient
    private List<SapData> homeSapData = new ArrayList<>();

    @Transient
    private List<SapData> foreignSapData = new ArrayList<>();

    public SapDataRun() {
        this.runIndex = 0;
        this.invoiceOwner = "";
        this.identifier = String.format("%s-%s", invoiceOwner, this.runIndex);
    }

    public SapDataRun(String invoiceOwner) {
        this.invoiceOwner = invoiceOwner;
        this.runIndex = 0;
        this.updateIdentifier();
    }

    public SapDataRun(Date desiredDate) {
        this.runIndex = 0;
        this.invoiceOwner = "";
        this.identifier = String.format("%s-%s", invoiceOwner, this.runIndex);
    }

    public SapDataRun withInvoiceOwner(String invoiceOwner) {
        this.invoiceOwner = invoiceOwner;
        this.identifier = String.format("%s-%s", invoiceOwner, this.runIndex);
        return this;
    }

    public SapDataRun withRunIndex(long runIndex) {
        this.runIndex = runIndex;
        this.identifier = String.format("%s-%s", invoiceOwner, this.runIndex);
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

    public void increaseSuccessfullSapData() {
        this.successfullSapData++;
    }

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

    public List<AvailableInvoice> getAvailableInvoices() {
        return availableInvoices;
    }

    public void setAvailableInvoices(List<AvailableInvoice> invoiceNumbers) {
        this.availableInvoices = invoiceNumbers;
    }

    public void addAvailableInvoice(AvailableInvoice invoiceNumber) {
        if (availableInvoices == null) {
            this.availableInvoices = new ArrayList<>();
        }
        this.availableInvoices.add(invoiceNumber);
    }

    public void setForeignSapData(List<SapData> foreignSapData) {
        this.foreignSapData = foreignSapData;
        this.numberForeignSapData = foreignSapData.size();
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
        this.invoices.removeIf(invoice -> !invoice.getOwner().getValue().equals(this.invoiceOwner));
        this.numberInvoices = invoices.size();
        for (Invoice invoice : this.invoices) {
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
        for (SapData sapData : sapDataList)
            addSapData(sapData, homeTaxKeys);
    }

    public void sortSapData() {
        Collections.sort(this.homeSapData);
        Collections.sort(this.foreignSapData);
    }

    public void addInvoice(Invoice invoice) {
        this.invoices.add(invoice);
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

    public List<SapData> retrieveSapData(String type) {
        if ("home".equals(type))
            return this.homeSapData;
        else if ("foreign".equals(type))
            return this.foreignSapData;
        else
            return new ArrayList<>();
    }

    public String log() {
        String logString = "runID: %s, date: %s, runIndex: %d, invoiceOwner %s, numberInvoices; %s, numberSapData: %s";
        return String.format(logString, this.identifier, new Date(), this.runIndex, this.invoiceOwner, this.invoices.size(),
                this.homeSapData.size());
    }

    public void updateIdentifier() {
        this.identifier = String.format("%s-%s", invoiceOwner, this.runIndex);
    }
}

package org.unidue.ub.libintel.almaconnector.model.run;

import org.unidue.ub.libintel.almaconnector.model.sap.SapResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * container object holding the list of SAP responses and the number of errors upon parsing or updating.
 */
public class SapResponseRun {

    private long numberOfErrors = 0;

    private long numberOfPoLineErrors = 0;

    private long numberOfInvoiceErrors = 0;

    private long numberOfReadErrors = 0;

    private String filename;

    private List<String> closedInvoices = new ArrayList<>();

    private List<String> closedPoLines = new ArrayList<>();

    private List<String> invoicesWithErrors = new ArrayList<>();

    private List<String> partialInvoices = new ArrayList<>();

    private List<String> poLinesWithErrors = new ArrayList<>();

    private List<SapResponse> responses = new ArrayList<>();

    public SapResponseRun() {}

    public long getNumberOfErrors() {
        return numberOfErrors;
    }

    public void setNumberOfErrors(int numberOfErrors) {
        this.numberOfErrors = numberOfErrors;
    }

    public long getNumberOfReadErrors() {
        return numberOfReadErrors;
    }

    public void setNumberOfReadErrors(int numberOfReadErrors) {
        this.numberOfReadErrors = numberOfReadErrors;
    }

    public List<SapResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<SapResponse> responses) {
        this.responses = responses;
    }

    public void addSapResponse(SapResponse sapResponse) {
        this.responses.add(sapResponse);
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getNumberOfResponses() {
        return responses.size();
    }

    public long getNumberOfPoLineErrors() {
        return numberOfPoLineErrors;
    }

    public void setNumberOfPoLineErrors(int numberOfPoLineErrors) {
        this.numberOfPoLineErrors = numberOfPoLineErrors;
    }

    public long getNumberOfInvoiceErrors() {
        return numberOfInvoiceErrors;
    }

    public void setNumberOfInvoiceErrors(int numberOfInvoiceErrors) {
        this.numberOfInvoiceErrors = numberOfInvoiceErrors;
    }

    public List<String> getClosedInvoices() {
        return closedInvoices;
    }

    public void setClosedInvoices(List<String> closedInvoices) {
        this.closedInvoices = closedInvoices;
    }

    public List<String> getClosedPoLines() {
        return closedPoLines;
    }

    public void setClosedPoLines(List<String> closedPoLines) {
        this.closedPoLines = closedPoLines;
    }

    public void setNumberOfErrors(long numberOfErrors) {
        this.numberOfErrors = numberOfErrors;
    }

    public void setNumberOfPoLineErrors(long numberOfPoLineErrors) {
        this.numberOfPoLineErrors = numberOfPoLineErrors;
    }

    public void setNumberOfInvoiceErrors(long numberOfInvoiceErrors) {
        this.numberOfInvoiceErrors = numberOfInvoiceErrors;
    }

    public void setNumberOfReadErrors(long numberOfReadErrors) {
        this.numberOfReadErrors = numberOfReadErrors;
    }

    public List<String> getInvoicesWithErrors() {
        return invoicesWithErrors;
    }

    public void setInvoicesWithErrors(List<String> invoicesWithErrors) {
        this.invoicesWithErrors = invoicesWithErrors;
    }

    public List<String> getPoLinesWithErrors() {
        return poLinesWithErrors;
    }

    public void setPoLinesWithErrors(List<String> poLinesWithErrors) {
        this.poLinesWithErrors = poLinesWithErrors;
    }

    public List<String> getPartialInvoices() {
        return partialInvoices;
    }

    public void setPartialInvoices(List<String> partialInvoices) {
        this.partialInvoices = partialInvoices;
    }

    public void addClosedPoLine(String poLineId) {
        this.closedPoLines.add(poLineId);
    }

    public void addClosedIncoice(String invoiceId) {
        this.closedInvoices.add(invoiceId);
    }

    public void addPoLineWithError(String poLineId) {
        this.poLinesWithErrors.add(poLineId);
    }

    public void addInvoiceWithError(String invoiceId) {
        this.invoicesWithErrors.add(invoiceId);
    }

    public void addPartialInvoice(String invoiceId) {
        this.partialInvoices.add(invoiceId);
    }

    public long increaseNumberOfErrors() {
        this.numberOfErrors++;
        return this.numberOfErrors;
    }

    public long increaseNumberOfReadErrors() {
        this.numberOfReadErrors++;
        return this.numberOfReadErrors;
    }

    public long increaseNumberOfPoLineErrors() {
        this.numberOfPoLineErrors++;
        return this.numberOfPoLineErrors;
    }

    public long increaseNumberOfInvoiceErrors() {
        this.numberOfInvoiceErrors++;
        return this.numberOfInvoiceErrors;
    }

    public String logString() {
        String logString = "filename: %s, numberOfResponses: %d, numberOfErrors: %d, numberOfReadErrors %d";
        return String.format(logString, filename, getNumberOfResponses(), numberOfErrors, numberOfReadErrors);
    }
}

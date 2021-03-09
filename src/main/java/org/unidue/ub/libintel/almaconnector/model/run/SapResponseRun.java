package org.unidue.ub.libintel.almaconnector.model.run;

import org.unidue.ub.libintel.almaconnector.model.sap.SapResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * container object holding the list of SAP responses and the number of errors upon parsing or updating.
 */
public class SapResponseRun {

    private int numberOfErrors = 0;

    private int numberOfReadErrors = 0;

    private String filename;

    private List<SapResponse> responses = new ArrayList<>();

    public SapResponseRun() {}

    public int getNumberOfErrors() {
        return numberOfErrors;
    }

    public void setNumberOfErrors(int numberOfErrors) {
        this.numberOfErrors = numberOfErrors;
    }

    public int getNumberOfReadErrors() {
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

    public long increaseNumberOfErrors() {
        this.numberOfErrors++;
        return this.numberOfErrors;
    }

    public long increaseNumberOfReadErrors() {
        this.numberOfReadErrors++;
        return this.numberOfReadErrors;
    }

    public String logString() {
        String logString = "filename: %s, numberOfResponses: %d, numberOfErrors: %d, numberOfReadErrors %d";
        return String.format(logString, filename, getNumberOfResponses(), numberOfErrors, numberOfReadErrors);
    }
}

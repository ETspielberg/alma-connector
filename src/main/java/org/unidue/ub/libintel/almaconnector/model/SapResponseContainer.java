package org.unidue.ub.libintel.almaconnector.model;

import java.util.ArrayList;
import java.util.List;

/**
 * container object holding the list of SAP responses and the number of errors upon parsing or updating.
 */
public class SapResponseContainer {

    private int numberOfErrors = 0;

    private List<SapResponse> responses = new ArrayList<>();

    public SapResponseContainer() {}

    public int getNumberOfErrors() {
        return numberOfErrors;
    }

    public void setNumberOfErrors(int numberOfErrors) {
        this.numberOfErrors = numberOfErrors;
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

    public long increaseNumberOfErrors() {
        this.numberOfErrors++;
        return this.numberOfErrors;
    }
}

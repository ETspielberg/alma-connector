package org.unidue.ub.libintel.almaconnector.model.run;

import lombok.Data;
import org.unidue.ub.libintel.almaconnector.model.sap.SapResponse;

import java.util.ArrayList;
import java.util.List;

@Data
public class SapImportRun {

    private long numberOfErrors = 0;

    private long numberOfPoLineErrors = 0;

    private long numberOfInvoiceErrors = 0;

    private long numberOfReadErrors = 0;

    private long numberOfStandingorders = 0;

    private String filename;

    private List<String> closedInvoices = new ArrayList<>();

    private List<String> closedPoLines = new ArrayList<>();

    private List<String> invoicesWithErrors = new ArrayList<>();

    private List<String> partialInvoices = new ArrayList<>();

    private List<String> poLinesWithErrors = new ArrayList<>();

    private List<String> standingPoLines = new ArrayList<>();

    private List<String> onetimePoLines = new ArrayList<>();

    private List<SapResponse> responses = new ArrayList<>();
}

package org.unidue.ub.libintel.almaconnector.service.alma;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.acq.PoLine;
import org.unidue.ub.alma.shared.acq.PoLineStatus;
import org.unidue.ub.alma.shared.acq.PoLines;
import org.unidue.ub.libintel.almaconnector.clients.acquisition.AlmaPoLinesApiClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class AlmaPoLineService {

    private final AlmaPoLinesApiClient almaPoLinesApiClient;

    /**
     * constructor based autowiring to the Feign client
     * @param almaPoLinesApiClient the Feign client for the Alma po line API
     */
    AlmaPoLineService(AlmaPoLinesApiClient almaPoLinesApiClient) {
        this.almaPoLinesApiClient = almaPoLinesApiClient;
    }

    /**
     * retrieves the active po-lines.
     * @return a list of po-lines
     */
    @Secured({ "ROLE_SYSTEM", "ROLE_ALMA" })
    public List<PoLine> getOpenPoLines() {
        // initialize parameters
        int batchSize = 100;
        int offset = 0;

        // retrieve first list of po-lines.
        PoLines poLines = this.almaPoLinesApiClient.getPoLines("application/json", "", "ACTIVE", batchSize, offset, "", "", "", "", "", "", "");
        List<PoLine> poLineList = new ArrayList<>(poLines.getPoLine());

        // as long as not all data are being collected, collect further
        while (poLineList.size() < poLines.getTotalRecordCount()) {
            offset += batchSize;
            poLines = this.almaPoLinesApiClient.getPoLines("application/json", "", "ACTIVE", batchSize, offset, "", "", "", "", "", "", "");
            poLineList.addAll(poLines.getPoLine());
        }
        return poLineList;
    }

    /**
     * retrieves the active po-lines.
     * @return a list of po-lines
     */
    @Secured({ "ROLE_SYSTEM", "ROLE_ALMA" , "ROLE_ALMA_Physical_Inventory_Operator"})
    public PoLine savePoLine(PoLine poLine) {
        return this.almaPoLinesApiClient.postAcqPoLines(poLine, "application/json", "");
    }

    @Secured({ "ROLE_SYSTEM", "ROLE_ALMA" , "ROLE_ALMA_Physical_Inventory_Operator"})
    public PoLine updatePoLine(PoLine poLine) {
        return this.almaPoLinesApiClient.putPoLinesPoLineId(poLine, "application/json", poLine.getNumber(),"" );
    }

    @Secured({ "ROLE_SYSTEM", "ROLE_ALMA" , "ROLE_ALMA_Physical_Inventory_Operator"})
    public PoLine getPoLine(String poLineId) {
        return this.almaPoLinesApiClient.getPoLinesPoLineId("application/json", poLineId);
    }

    public boolean closePoLine(PoLine poLine) {
        poLine.setStatus(new PoLineStatus().value("CLOSED"));
        poLine = this.almaPoLinesApiClient.putPoLinesPoLineId(poLine, "application/json", poLine.getNumber(), "false");
        return "CLOSED".equals(poLine.getStatus().getValue());
    }

}

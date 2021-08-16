package org.unidue.ub.libintel.almaconnector.service.alma;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.*;
import org.unidue.ub.libintel.almaconnector.clients.alma.bib.AlmaCatalogApiClient;

/**
 * offers functions around bibliographic entries in Alma
 *
 * @author Eike Spielberg
 * @author eike.spielberg@uni-due.de
 * @version 1.0
 */
@Service
@Slf4j
public class AlmaCatalogService {

    private final AlmaCatalogApiClient almaCatalogApiClient;

    /**
     * Constructor based autowiring of the alma bib api feign client to the service
     * @param almaCatalogApiClient the alma bib api feign client
     */
    AlmaCatalogService(AlmaCatalogApiClient almaCatalogApiClient) {
        this.almaCatalogApiClient = almaCatalogApiClient;
    }

    /**
     * updates the call number in a holding record
     * @param mmsId the MMS ID of the bibliographic record of the holding
     * @param holdingId the ID of the holding to be updated
     * @param callNo the new call number to be inserted into the holding record
     */
    public void updateCallNoInHolding(String mmsId, String holdingId, String callNo) {
        // retrieve holding record for MMS und holding ID from the Alma bib API
        HoldingWithRecord holding = this.almaCatalogApiClient.getBibsMmsIdHoldingsHoldingId(mmsId, holdingId);
        log.debug(holding.getRecord().getLeader());
        // initialize boolean indicating whether a particular field has been set.
        boolean isSet = false;

        // check fields
        for (MarcDatafield field : holding.getRecord().getDatafield()) {
            if ("852".equals(field.getTag())) {
                for (MarcSubfield subfield : field.getSubfield())
                    // if 852h field is found, insert the provided call number
                    if ("h".equals(subfield.getCode())) {
                        subfield.setValue(callNo);
                        isSet = true;
                        break;
                    }
                if (!isSet) {
                    // if the subfield does not yet exist, add a new subfield of code h and add the provided call number
                    field.getSubfield().add(new MarcSubfield().code("h").value(callNo));
                    break;
                }

            }
        }
        // use the ALma bib API to update the holding.
        this.almaCatalogApiClient.putBibsMmsIdHoldingsHoldingId(mmsId, holdingId, holding);
    }


    /**
     * retrieves a bibliographic record by its mms id
     * @param mmsId the mms id of the bibliographic record to be retrieved
     * @return the <class>BibWithRecord</class> class holding the bibliographic record
     */
    public BibWithRecord getRecord(String mmsId) {
        return this.almaCatalogApiClient.getBibsMmsId(mmsId, "full", "e_avail");
    }

    /**
     * checks whether or not portfolios exist for a given mms id
     * @param mmsId the mms id of the record to be checked for portfolios
     * @return true, if at least one portfolio exists for the given mms id
     */
    public boolean isPortfolios(String mmsId) {
        Portfolios portfolios = this.almaCatalogApiClient.getBibsMmsIdPortfolios(mmsId, 1, 0);
        return portfolios.getTotalRecordCount() > 0;
    }

}

package org.unidue.ub.libintel.almaconnector.service.alma;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.*;
import org.unidue.ub.libintel.almaconnector.clients.bib.AlmaCatalogApiClient;


@Service
public class AlmaCatalogService {

    private final AlmaCatalogApiClient almaCatalogApiClient;

    private final Logger log = LoggerFactory.getLogger(AlmaCatalogService.class);

    AlmaCatalogService(AlmaCatalogApiClient almaCatalogApiClient) {
        this.almaCatalogApiClient = almaCatalogApiClient;
    }

    public boolean updateCallNoInHolding(String mmsId, String holdingId, String callNo) {
            HoldingWithRecord holding = this.almaCatalogApiClient.getBibsMmsIdHoldingsHoldingId(mmsId, holdingId);
            log.info(holding.getRecord().getLeader());
            for (MarcDatafield field: holding.getRecord().getDatafield()) {
                if ("852".equals(field.getTag()))
                    for (MarcSubfield subfield : field.getSubfield())
                        if ("h".equals(subfield.getCode()))
                            return false;
                    field.getSubfield().add(new MarcSubfield().code("h").value(callNo));
            }
            this.almaCatalogApiClient.putBibsMmsIdHoldingsHoldingId(mmsId, holdingId, holding);
            return true;
    }

    public int getNumberOfPortfolios(String mmsId) {
        return this.almaCatalogApiClient.getBibsMmsIdPortfolios(mmsId, 1, 0).getTotalRecordCount();
    }

    public BibWithRecord getRecord(String mmsId) {
        return this.almaCatalogApiClient.getBibsMmsId(mmsId, "full", "e_avail");
    }

}

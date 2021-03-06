package org.unidue.ub.libintel.almaconnector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.*;
import org.unidue.ub.libintel.almaconnector.clients.bib.AlmaCatalogApiClient;


@Service
public class CatalogService {

    private final AlmaCatalogApiClient almaCatalogApiClient;

    private final Logger log = LoggerFactory.getLogger(CatalogService.class);

    CatalogService(AlmaCatalogApiClient almaCatalogApiClient) {
        this.almaCatalogApiClient = almaCatalogApiClient;
    }

    public boolean updateCallNoInHolding(String mmsId, String holdingId, String callNo) {
            HoldingWithRecord holding = this.almaCatalogApiClient.getBibsMmsIdHoldingsHoldingId(mmsId, holdingId);
            log.info(holding.getRecord().getLeader());
            for (MarcDatafield field: holding.getRecord().getDatafield()) {
                if ("852".equals(field.getTag()))
                    field.getSubfield().add(new MarcSubfield().code("h").value(callNo));
            }
            this.almaCatalogApiClient.putBibsMmsIdHoldingsHoldingId(mmsId, holdingId, holding);
            return true;
    }

}

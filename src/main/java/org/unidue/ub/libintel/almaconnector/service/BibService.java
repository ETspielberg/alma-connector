package org.unidue.ub.libintel.almaconnector.service;

import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.Item;
import org.unidue.ub.libintel.almaconnector.clients.bib.AlmaCatalogApiClient;


@Service
public class BibService {

    public final AlmaCatalogApiClient almaCatalogApiClient;

    public BibService(AlmaCatalogApiClient almaCatalogApiClient) {
        this.almaCatalogApiClient = almaCatalogApiClient;
    }


}

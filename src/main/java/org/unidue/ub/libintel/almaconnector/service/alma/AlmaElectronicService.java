package org.unidue.ub.libintel.almaconnector.service.alma;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.*;
import org.unidue.ub.libintel.almaconnector.clients.electronic.AlmaElectronicApiClient;

@Service
public class AlmaElectronicService {

    private final AlmaElectronicApiClient almaElectronicApiClient;

    @Value("${libintel.alma.diss.collection.id}")
    private String dissCollectionId;

    @Value("${libintel.alma.diss.service.id}")
    private String dissServiceId;

    AlmaElectronicService(AlmaElectronicApiClient almaElectronicApiClient) {
        this.almaElectronicApiClient = almaElectronicApiClient;
    }

    public Portfolio createDissPortfolio(String mmsId, String url) {
        Portfolio portfolio = new Portfolio()
                .linkingDetails(new LinkingDetails().staticUrl(url))
                .availability(new PortfolioAvailability().value("11"))
                .isLocal(true)
                .resourceMetadata(new ResourceMetadata().mmsId(new ResourceMetadata2MmsId().value(mmsId)))
                .materialType(new PortfolioMaterialType().value("DISSERTATION"));
        return this.almaElectronicApiClient.createElectronicPortfolio(dissCollectionId, dissServiceId, portfolio);
    }
}

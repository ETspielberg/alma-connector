package org.unidue.ub.libintel.almaconnector.service.alma;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.*;
import org.unidue.ub.libintel.almaconnector.clients.alma.electronic.AlmaElectronicApiClient;

/**
 * offers functions around portfolios, services, and collections in Alma
 *
 * @author Eike Spielberg
 * @author eike.spielberg@uni-due.de
 * @version 1.0
 */
@Service
@Slf4j
public class AlmaElectronicService {

    private final AlmaElectronicApiClient almaElectronicApiClient;

    @Value("${libintel.alma.diss.collection.id}")
    private String dissCollectionId;

    @Value("${libintel.alma.diss.service.id}")
    private String dissServiceId;

    /**
     * Constructor based autowiring of alma electronic api feign client
     * @param almaElectronicApiClient the alma electronic api feign client
     */
    AlmaElectronicService(AlmaElectronicApiClient almaElectronicApiClient) {
        this.almaElectronicApiClient = almaElectronicApiClient;
    }

    /**
     * creates a portfolio representation of a dissertation and adds it to the dissertation collection
     * @param mmsId the mms id of the dissertation record
     * @param url the url of the portfolio
     */
    public void createDissPortfolio(String mmsId, String url) {
        LinkingDetails linkingDetails = new LinkingDetails()
                .urlType(new LinkingDetailsUrlType().value("static"))
                .staticUrl("jkey=" + url)
                .url("jkey=" + url)
                .proxyEnabled(new LinkingDetailsProxyEnabled().value("false"));
        Portfolio portfolio = new Portfolio()
                .linkingDetails(linkingDetails)
                .availability(new PortfolioAvailability().value("11"))
                .isLocal(true)
                .resourceMetadata(new ResourceMetadata().mmsId(new ResourceMetadata2MmsId().value(mmsId)))
                .materialType(new PortfolioMaterialType().value("DISSERTATION"));
        this.almaElectronicApiClient.createElectronicPortfolio(dissCollectionId, dissServiceId, portfolio);
    }
}

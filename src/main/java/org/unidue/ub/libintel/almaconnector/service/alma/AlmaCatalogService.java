package org.unidue.ub.libintel.almaconnector.service.alma;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.*;
import org.unidue.ub.libintel.almaconnector.clients.alma.bib.AlmaCatalogApiClient;

import java.util.List;

import static org.unidue.ub.libintel.almaconnector.service.alma.AlmaElectronicService.getLinkingDetails;

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

    @Value("${doi.resolver.url:https://doi.org/}")
    private String doiUrl;

    @Value("${mycore.resolver.url:https://duepublico2.uni-due.de/receive/duepublico_mods_}")
    private String duepublicoUrl;

    @Value("${libintel.alma.collections.oa.collection.id}")
    private String oaCollectionId;

    @Value("${libintel.alma.collections.duepublico.collection.id}")
    private String duepublicoCollectionId;


    /**
     * Constructor based autowiring of the alma bib api feign client to the service
     *
     * @param almaCatalogApiClient the alma bib api feign client
     */
    AlmaCatalogService(AlmaCatalogApiClient almaCatalogApiClient) {
        this.almaCatalogApiClient = almaCatalogApiClient;
    }

    /**
     * updates the call number in a holding record
     *
     * @param mmsId     the MMS ID of the bibliographic record of the holding
     * @param holdingId the ID of the holding to be updated
     * @param callNo    the new call number to be inserted into the holding record
     */
    public void updateCallNoInHolding(String mmsId, String holdingId, String callNo) {
        // retrieve holding record for MMS und holding ID from the Alma bib API
        HoldingWithRecord holding = this.almaCatalogApiClient.getBibsMmsIdHoldingsHoldingId(mmsId, holdingId);
        log.debug(holding.getRecord().getLeader());
        // initialize boolean indicating whether a particular field has been set.
        boolean isSet = false;
        // check if it is a ZDB holding. If it is, just quit
        if (isZdbHolding(holding)) return;
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
        this.almaCatalogApiClient.putBibsMmsIdHoldingsHoldingId(holding, mmsId, holdingId);
    }


    /**
     * retrieves a bibliographic record by its mms id
     *
     * @param mmsId the mms id of the bibliographic record to be retrieved
     * @return the <class>BibWithRecord</class> class holding the bibliographic record
     */
    public BibWithRecord getRecord(String mmsId) {
        return this.almaCatalogApiClient.getBibsMmsId(mmsId, "full", "e_avail");
    }

    /**
     * checks whether or not portfolios exist for a given mms id
     *
     * @param mmsId the mms id of the record to be checked for portfolios
     * @return true, if at least one portfolio exists for the given mms id
     */
    public boolean isPortfolios(String mmsId) {
        Portfolios portfolios = this.almaCatalogApiClient.getBibsMmsIdPortfolios(mmsId, 1, 0);
        return portfolios.getTotalRecordCount() > 0;
    }

    /**
     * retrieves the number of items for a given holding
     *
     * @param mmsId     the mms id of the record
     * @param holdingId the holding id
     * @return the number of items in this holding
     */
    public long getNumberOfItems(String mmsId, String holdingId) {
        return this.almaCatalogApiClient.getBibsMmsIdHoldingsHoldingIdItems(mmsId, holdingId, 1, 0, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "").getTotalRecordCount();
    }

    private boolean isZdbHolding(HoldingWithRecord holding) {
        for (MarcDatafield field : holding.getRecord().getDatafield()) {
            if ("990".equals(field.getTag())) {
                for (MarcSubfield subfield : field.getSubfield())
                    if ("a".equals(subfield.getCode())) {
                        if (subfield.getValue().contains("ZDB") || "ZDB".equals(subfield.getValue()))
                            return true;
                    }
            }
        }
        return false;
    }

    /**
     * updates field 024 of the bibliographic record of the given mms with the identifier provided.
     * also creates the corresponding link in field 856
     * @param mmsId the mms id of the record to be updated
     * @param identifier the identifier to be updated in the record
     * @param type the type of identifier. Currently, supported are doi and duepublico
     */
    public void updateIdentifier(String mmsId, String identifier, String type) {
        BibWithRecord bib = this.getRecord(mmsId);
        if (bib == null || bib.getRecord() == null) {
            log.warn(String.format("could not add identifier for mms id %s. No record found", mmsId));
            return;
        }
        MarcRecord marcRecord = bib.getRecord();
        switch (type) {
            case "doi": {
                MarcDatafield marcDatafieldId = new MarcDatafield().ind1("7").ind2(" ").tag("024");
                marcDatafieldId.addSubfield(new MarcSubfield().code("a").value(identifier));
                marcDatafieldId.addSubfield(new MarcSubfield().code("2").value("doi"));
                marcRecord.addDatafield(marcDatafieldId);
                MarcDatafield marcDatafieldUrl = new MarcDatafield().ind1("4").ind2("0").tag("856");
                marcDatafieldUrl.addSubfield(new MarcSubfield().code("a").value(doiUrl + identifier));
                marcRecord.addDatafield(marcDatafieldUrl);
                break;
            }
            case "duepublico": {
                MarcDatafield marcDatafieldId = new MarcDatafield().ind1("8").ind2(" ").tag("024");
                marcDatafieldId.addSubfield(new MarcSubfield().code("a").value(identifier));
                marcRecord.addDatafield(marcDatafieldId);
                MarcDatafield marcDatafieldUrl = new MarcDatafield().ind1("4").ind2("0").tag("856");
                marcDatafieldUrl.addSubfield(new MarcSubfield().code("a").value(duepublicoUrl + identifier));
                marcRecord.addDatafield(marcDatafieldUrl);
            }
        }
        this.almaCatalogApiClient.putBibsMmsId(bib, mmsId, "", "", "", "", "", "", "");
    }

    public boolean updateOaPortfolio(String mmsId, String doi) {
        List<Portfolio> portfolios = this.getPortfolios(mmsId);
        if (portfolios.size() > 0) {
            for (Portfolio portfolio : portfolios) {
                if (portfolio.getElectronicCollection().getId().getValue().equals(this.oaCollectionId)) {
                    if (portfolio.getLinkingDetails() != null)
                        portfolio.getLinkingDetails().setStaticUrl(this.doiUrl + doi);
                    else
                        portfolio.setLinkingDetails(getLinkingDetails(this.doiUrl + doi));
                    portfolio.setMaterialType(new PortfolioMaterialType().value("DOCUMENT"));
                    this.updatePortfolio(mmsId, portfolio);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean updateMycorePortfolio(String mmsId, String duepublicoId) {
        List<Portfolio> portfolios = this.getPortfolios(mmsId);
        if (portfolios.size() > 0) {
            for (Portfolio portfolio : portfolios) {
                if (portfolio.getElectronicCollection().getId().getValue().equals(this.duepublicoCollectionId)) {
                    if (portfolio.getLinkingDetails() != null)
                        portfolio.getLinkingDetails().setStaticUrl(this.duepublicoUrl + duepublicoId);
                    else
                        portfolio.setLinkingDetails(getLinkingDetails(this.duepublicoUrl + duepublicoId));
                    this.updatePortfolio(mmsId, portfolio);
                    return true;
                }
            }
        }
        return false;
    }

    public Portfolio updatePortfolio(String mmsId, Portfolio portfolio) {
        return this.almaCatalogApiClient.putBibsMmsIdPortfoliosPortfolioId(mmsId, portfolio.getId(), portfolio);
    }

    public List<Portfolio> getPortfolios(String mmsId) {
        int limit = 5;
        int offset = 0;
        Portfolios portfolios = this.almaCatalogApiClient.getBibsMmsIdPortfolios(mmsId, limit, offset);
        int numberOfPortfolios = portfolios.getTotalRecordCount();
        List<Portfolio> portfolioList = portfolios.getPortfolio();
        while(offset + limit < numberOfPortfolios) {
            offset += limit;
            portfolios = this.almaCatalogApiClient.getBibsMmsIdPortfolios(mmsId, limit, offset);
            portfolioList.addAll(portfolios.getPortfolio());
        }
        return portfolioList;
    }
}

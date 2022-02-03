package org.unidue.ub.libintel.almaconnector.service;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.BibWithRecord;
import org.unidue.ub.alma.shared.bibs.HookUserRequest;
import org.unidue.ub.alma.shared.bibs.Item;
import org.unidue.ub.alma.shared.user.AlmaUser;
import org.unidue.ub.libintel.almaconnector.clients.getter.GetterClient;
import org.unidue.ub.libintel.almaconnector.model.hook.HookEventTypes;
import org.unidue.ub.libintel.almaconnector.model.hook.LoanHook;
import org.unidue.ub.libintel.almaconnector.model.hook.RequestHook;
import org.unidue.ub.libintel.almaconnector.model.EventType;
import org.unidue.ub.libintel.almaconnector.model.media.elasticsearch.EsEvent;
import org.unidue.ub.libintel.almaconnector.model.media.elasticsearch.EsItem;
import org.unidue.ub.libintel.almaconnector.model.media.elasticsearch.EsPrintManifestation;
import org.unidue.ub.libintel.almaconnector.model.openaccess.ApcStatistics;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaCatalogService;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class GetterService {

    private final AlmaCatalogService almaCatalogService;

    private final GetterClient getterClient;

    GetterService(AlmaCatalogService almaCatalogService,
                  GetterClient getterClient) {
        this.almaCatalogService = almaCatalogService;
        this.getterClient = getterClient;
    }

    /**
     * saves a manifestation to the index. If it does not exist, then a new entry is created.
     * @param esPrintManifestation the manifestation to be saved
     * @return the saved manifestation
     */
    public EsPrintManifestation index(EsPrintManifestation esPrintManifestation) {
        try {
            return this.getterClient.saveManifestation(esPrintManifestation);
        } catch (FeignException feignException) {
            log.warn(String.format("could not save print manifestation %s, message: %s",
                    esPrintManifestation.getTitleID(),
                    feignException.getMessage()),feignException);
            return null;
        }
    }

    /**
     * saves a new item to elasticsearch
     * @param almaItem the new item to be saved
     * @param updateDate the update date of the item webhook
     */
    public void index(Item almaItem, Date updateDate) {
        String mmsId = almaItem.getBibData().getMmsId();

        // set inventory date from item. if there is no inventory date set, use the update date as taken from the webhook
        Date inventoryDate = almaItem.getItemData().getInventoryDate();
        if (inventoryDate == null)
            inventoryDate = updateDate;
        EsItem esItem = new EsItem(almaItem, inventoryDate);
        EsPrintManifestation esPrintManifestation = findManifestationByItem(almaItem);
        if (esPrintManifestation == null) {
            BibWithRecord bib = this.almaCatalogService.getRecord(almaItem.getBibData().getMmsId());
            if (bib == null) {
                log.error("no record available for mms id " + mmsId);
                return;
            }
            esPrintManifestation = new EsPrintManifestation(bib);
        }
        esPrintManifestation.addItem(esItem);
        index(esPrintManifestation);
    }


    /**
     * updates an item in the elasticsearch index
     * @param almaItem the item to be updated
     * @param updateDate the update from the webhook
     */
    public void updateItem(Item almaItem, Date updateDate) {
        EsPrintManifestation esPrintManifestation = findManifestationByItem(almaItem);
        if (esPrintManifestation == null)
            return;
        EsItem esItem = esPrintManifestation.findCorrespindingItem(almaItem);
        if (esItem == null) {
            this.index(almaItem, updateDate);
        } else {
            boolean isChanged  = esItem.update(almaItem);
            if (isChanged)
                this.index(esPrintManifestation);
        }
    }

    /**
     * sets the deletion date for an item in the elasticsearch index
     * @param almaItem  the item to be deleted
     * @param date the update date from the webhook
     */
    public void deleteItem(Item almaItem, Date date) {
        if (almaItem.getBibData() == null || almaItem.getBibData().getMmsId() == null)
            return;
        String mmsId = almaItem.getBibData().getMmsId();
        EsPrintManifestation esPrintManifestation = retrieveOrBuildManifestation(mmsId, almaItem, date);
        if (esPrintManifestation == null)
            return;
        EsItem esItem = esPrintManifestation.findCorrespindingItem(almaItem);
        esItem.delete(date);
        this.index(esPrintManifestation);
    }

    /**
     * saves a request event to the index or closes one.
     * @param hook the request webhook
     * @param almaItem the item of the corresponding request
     */
    public void indexRequest(RequestHook hook, Item almaItem) {
        String eventType = hook.getEvent().getValue();
        HookUserRequest userRequest = hook.getUserRequest();
        String mmsId = userRequest.getMmsId();
        Date requestDate = userRequest.getRequestDate();
        EsPrintManifestation esPrintManifestation = retrieveOrBuildManifestation(mmsId, almaItem, hook.getTime());
        if (esPrintManifestation == null)
            return;
        EsItem esItem = esPrintManifestation.findCorrespindingItem(almaItem);
        if (EventType.REQUEST_CREATED.name().equals(eventType))
            esItem.addEvent(new EsEvent(userRequest.getRequestid(), requestDate, null, EventType.REQUEST, ""));
        else if (HookEventTypes.REQUEST_CLOSED.name().equals(eventType))
            esItem.closeRequest(new Date(hook.getUserRequest().getRequestDate().getTime()));
        this.index(esPrintManifestation);
    }

    /**
     * saves a loan event to the index or closes one.
     * @param hook the loan webhook
     * @param almaItem the item of the corresponding loan
     * @param user the user who performed the loan
     */
    public void indexLoan(LoanHook hook, Item almaItem, AlmaUser user) {
        String eventType = hook.getEvent().getValue();
        String mmsId = hook.getItemLoan().getMmsId();
        EsPrintManifestation esPrintManifestation = retrieveOrBuildManifestation(mmsId, almaItem, hook.getTime());
        if (esPrintManifestation == null)
            return;
        EsItem esItem = esPrintManifestation.findCorrespindingItem(almaItem);
        if (HookEventTypes.LOAN_CREATED.name().equals(eventType))
            esItem.addEvent(new EsEvent(hook.getItemLoan().getLoanId(), hook.getTime(), null, EventType.LOAN, user.getUserGroup().getValue()));
        else if (HookEventTypes.LOAN_RETURNED.name().equals(eventType))
            esItem.closeLoan(hook.getTime());
        this.index(esPrintManifestation);
    }


    private EsPrintManifestation findManifestationByMmsId(String mmsId) {
        List<EsPrintManifestation> hits = this.getterClient.getManifestations("multipleIds", mmsId);
        log.debug(String.format("manifestations with %s found to be updated: %d", mmsId, hits.size()));
        return (hits.size() == 0) ? null : hits.get(0);
    }

    private EsPrintManifestation findManifestationByItem(Item item) {
        List<EsPrintManifestation> printManifestations = this.getterClient.getManifestations("barcode", (item.getItemData().getBarcode()));
        if (printManifestations.size() == 0)
            printManifestations = this.getterClient.getManifestations("shelfmark", item.getItemData().getAlternativeCallNumber());

        // retrieve full document
        if (printManifestations.size() > 0)
            return printManifestations.get(0);
        else
            return null;
    }

    private EsPrintManifestation retrieveOrBuildManifestation(String mmsId, Item almaItem, Date updateDate) {
        EsPrintManifestation esPrintManifestation = this.findManifestationByMmsId(mmsId);

        if (esPrintManifestation == null) {
            log.debug("retrieved manifestation from elasticsearch is null. Building new manifestation from mms id " + mmsId);
            BibWithRecord bib = this.almaCatalogService.getRecord(mmsId);
            if (bib == null) {
                log.error("no record available for mms id " + mmsId);
                return null;
            }
            esPrintManifestation = new EsPrintManifestation(bib);
        }
        log.debug("retrieved manifestation from elasticsearch: " + esPrintManifestation.getTitleID());
        EsItem esItem = esPrintManifestation.findCorrespindingItem(almaItem);
        if (esItem == null) {
            log.debug("could not find corresponding item in manifestation. Building new one.");
            esItem = new EsItem(almaItem, updateDate);
            esPrintManifestation.addItem(esItem);
            esPrintManifestation = this.index(esPrintManifestation);
        }
        return esPrintManifestation;
    }

    public ApcStatistics indexApcStatistics(ApcStatistics apcStatistics) {
        return this.getterClient.saveApcStatistics(apcStatistics);
    }
}

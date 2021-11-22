package org.unidue.ub.libintel.almaconnector.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.BibWithRecord;
import org.unidue.ub.alma.shared.bibs.HookUserRequest;
import org.unidue.ub.alma.shared.bibs.Item;
import org.unidue.ub.alma.shared.user.AlmaUser;
import org.unidue.ub.libintel.almaconnector.model.hook.HookEventTypes;
import org.unidue.ub.libintel.almaconnector.model.hook.LoanHook;
import org.unidue.ub.libintel.almaconnector.model.hook.RequestHook;
import org.unidue.ub.libintel.almaconnector.model.EventType;
import org.unidue.ub.libintel.almaconnector.model.media.elasticsearch.EsEvent;
import org.unidue.ub.libintel.almaconnector.model.media.elasticsearch.EsItem;
import org.unidue.ub.libintel.almaconnector.model.media.elasticsearch.EsPrintManifestation;
import org.unidue.ub.libintel.almaconnector.repository.elasticsearch.ManifestationRepository;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaCatalogService;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ElasticsearchService {

    private final ManifestationRepository manifestationRepository;

    private final AlmaCatalogService almaCatalogService;

    ElasticsearchService(ManifestationRepository manifestationRepository,
                         AlmaCatalogService almaCatalogService) {
        this.almaCatalogService = almaCatalogService;
        this.manifestationRepository = manifestationRepository;
    }

    public EsPrintManifestation index(EsPrintManifestation esPrintManifestation) {
        return this.manifestationRepository.save(esPrintManifestation);
    }

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

    public void update(EsPrintManifestation esPrintManifestation, String id) {
        this.manifestationRepository.save(esPrintManifestation);
    }

    private EsPrintManifestation findManifestationByMmsId(String mmsId) {
        List<EsPrintManifestation> hits = this.manifestationRepository.findManifestationByTitleIDOrAlmaId(mmsId, mmsId);
        log.debug(String.format("manifestations with %s found to be updated: %d", mmsId, hits.size()));
        return (hits.size() == 0) ? null : hits.get(0);
    }

    private EsPrintManifestation findManifestationByItem(Item item) {
        EsPrintManifestation printManifestation = this.manifestationRepository.retrieveByBarcode(item.getItemData().getBarcode());
        if (printManifestation == null)
            printManifestation = this.manifestationRepository.retrieveByShelfmark(item.getItemData().getAlternativeCallNumber());
        return printManifestation;
    }

    public void deleteItem(Item almaItem, Date date) {
        String mmsId = almaItem.getBibData().getMmsId();
        EsPrintManifestation esPrintManifestation = retrieveOrBuildManifestation(mmsId, almaItem, date);
        if (esPrintManifestation == null)
            return;
        EsItem esItem = esPrintManifestation.findCorrespindingItem(almaItem);
        esItem.delete(date);
        this.index(esPrintManifestation);
    }

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

    public void indexLoan(LoanHook hook, Item almaItem, AlmaUser user) {
        String eventType = hook.getEvent().getValue();
        String mmsId = hook.getItemLoan().getMmsId();
        EsPrintManifestation esPrintManifestation = retrieveOrBuildManifestation(mmsId, almaItem, hook.getTime());
        if (esPrintManifestation == null)
            return;
        EsItem esItem = esPrintManifestation.findCorrespindingItem(almaItem);
        if (HookEventTypes.LOAN_CREATED.name().equals(eventType))
            esItem.addEvent(new EsEvent(hook.getItemLoan().getLoanId(), new Date(hook.getItemLoan().getLoanDate().toInstant().toEpochMilli()), null, EventType.LOAN, user.getUserGroup().getValue()));
        else if (HookEventTypes.LOAN_RETURNED.name().equals(eventType))
            esItem.closeLoan(new Date(hook.getItemLoan().getLoanDate().toInstant().toEpochMilli()));
        this.index(esPrintManifestation);
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
}

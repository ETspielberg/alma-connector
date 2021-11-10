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

    public void index(EsPrintManifestation esPrintManifestation) {
        this.manifestationRepository.save(esPrintManifestation);
    }

    public void index(Item almaItem, Date updateDate) {
        String mmsId = almaItem.getBibData().getMmsId();

        // set inventory date from item. if there is no inventory date set, use the update date as taken from the webhook
        Date inventoryDate = almaItem.getItemData().getInventoryDate();
        if (inventoryDate == null)
            inventoryDate = updateDate;
        EsItem esItem = new EsItem(almaItem, inventoryDate);
        EsPrintManifestation esPrintManifestation = findManifestationByMmsId(mmsId);
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

    public void updateManifestation(EsPrintManifestation esPrintManifestation) {
        this.manifestationRepository.save(esPrintManifestation);
    }

    private EsPrintManifestation findManifestationByMmsId(String mmsId) {
        List<EsPrintManifestation> hits = this.manifestationRepository.findManifestationByTitleIDOrAlmaId(mmsId, mmsId);
        return (hits.size() == 0) ? null : hits.get(0);
    }

    public void deleteItem(Item almaItem, Date date) {
        String mmsId = almaItem.getBibData().getMmsId();
        EsPrintManifestation esPrintManifestation = retrieveOrBuildManifestation(mmsId, almaItem, date);
        if (esPrintManifestation == null)
            return;
        EsItem esItem = esPrintManifestation.getItem(almaItem.getItemData().getPid());
        esItem.delete(date);
        this.updateManifestation(esPrintManifestation);
    }

    public void updateItem(Item almaItem, Date updateDate) {
        String mmsId = almaItem.getBibData().getMmsId();
        EsPrintManifestation esPrintManifestation = retrieveOrBuildManifestation(mmsId, almaItem, updateDate);
        if (esPrintManifestation == null)
            return;
        EsItem esItem = esPrintManifestation.findCorrespindingItem(almaItem);
        if (esItem == null) {
            this.index(almaItem, updateDate);
        } else {
            esItem.update(almaItem);
            this.updateManifestation(esPrintManifestation);
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
        EsItem esItem = esPrintManifestation.getItem(almaItem.getItemData().getPid());
        if (EventType.REQUEST_CREATED.name().equals(eventType))
            esItem.addEvent(new EsEvent(userRequest.getRequestid(), requestDate, null, EventType.REQUEST, ""));
        else if (HookEventTypes.REQUEST_CLOSED.name().equals(eventType))
        this.updateManifestation(esPrintManifestation);
    }

    public void indexLoan(LoanHook hook, Item almaItem, AlmaUser user) {
        String eventType = hook.getEvent().getValue();
        String mmsId = hook.getItemLoan().getMmsId();
        EsPrintManifestation esPrintManifestation = retrieveOrBuildManifestation(mmsId, almaItem, hook.getTime());
        if (esPrintManifestation == null)
            return;
        EsItem esItem = esPrintManifestation.getItem(almaItem.getItemData().getPid());
        if (HookEventTypes.LOAN_CREATED.name().equals(eventType))
            esItem.addEvent(new EsEvent(hook.getItemLoan().getLoanId(), new Date(hook.getItemLoan().getLoanDate().toInstant().toEpochMilli()), null, EventType.LOAN, user.getUserGroup().getValue()));
        else if (HookEventTypes.LOAN_RETURNED.name().equals(eventType))
            esItem.closeLoan(new Date(hook.getItemLoan().getLoanDate().toInstant().toEpochMilli()));
        this.updateManifestation(esPrintManifestation);
    }

    private EsPrintManifestation retrieveOrBuildManifestation(String mmsId, Item almaItem, Date updateDate) {
        EsPrintManifestation esPrintManifestation = findManifestationByMmsId(mmsId);
        if (esPrintManifestation == null) {
            BibWithRecord bib = this.almaCatalogService.getRecord(mmsId);
            if (bib == null) {
                log.error("no record available for mms id " + mmsId);
                return null;
            }
            esPrintManifestation = new EsPrintManifestation(bib);
        }
        EsItem esItem = esPrintManifestation.findCorrespindingItem(almaItem);
        if (esItem == null) {
            esItem = new EsItem(almaItem, updateDate);
            esPrintManifestation.addItem(esItem);
            this.index(esPrintManifestation);
        }
        return esPrintManifestation;
    }

    private int getDelta(String eventType) {
        switch (eventType) {
            case "BIB_CREATED":
            case "ITEM_CREATED":
            case "LOAN_CREATED":
            case "REQUEST_CREATED":
                return 1;
            case "BIB_DELETED":
            case "ITEM_DELETED":
            case "REQUEST_CLOSED":
            case "LOAN_RETURNED":
                return -1;
            default:
                return 0;
        }
    }


}

package org.unidue.ub.libintel.almaconnector.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.*;
import org.unidue.ub.alma.shared.user.Address;
import org.unidue.ub.alma.shared.user.AlmaUser;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrderLine;
import org.unidue.ub.libintel.almaconnector.model.hook.*;
import org.unidue.ub.libintel.almaconnector.service.alma.*;
import org.unidue.ub.libintel.almaconnector.service.bubi.BubiOrderLineService;

import java.util.concurrent.TimeUnit;


/**
 * offers functions for processing different types of alma web hooks
 *
 * @author Eike Spielberg
 * @author eike.spielberg@uni-due.de
 * @version 1.0
 */
@Service
@Slf4j
public class HookService {

    private final AlmaUserService almaUserService;

    private final AlmaItemService almaItemService;

    private final AlmaCatalogService almaCatalogService;

    private final AlmaElectronicService almaElectronicService;

    private final BubiOrderLineService bubiOrderLineService;

    private final AlmaInvoiceService almaInvoiceService;

    private final RegalfinderService regalfinderService;

    private final RedisService redisService;

    private final GetterService getterService;

    /**
     * constructor based autowiring to the individual services
     *
     * @param almaUserService       the alma user api feign client
     * @param almaItemService       the alma item api feign client
     * @param almaCatalogService    the alma bib api feign client
     * @param bubiOrderLineService  the bubi order line service
     * @param almaElectronicService the alma electronic api feign client
     */
    HookService(AlmaUserService almaUserService,
                AlmaItemService almaItemService,
                AlmaCatalogService almaCatalogService,
                BubiOrderLineService bubiOrderLineService,
                AlmaElectronicService almaElectronicService,
                AlmaInvoiceService almaInvoiceService,
                RegalfinderService regalfinderService,
                RedisService redisService,
                GetterService getterService) {
        this.almaUserService = almaUserService;
        this.almaItemService = almaItemService;
        this.almaCatalogService = almaCatalogService;
        this.almaElectronicService = almaElectronicService;
        this.bubiOrderLineService = bubiOrderLineService;
        this.almaInvoiceService = almaInvoiceService;
        this.regalfinderService = regalfinderService;
        this.redisService = redisService;
        this.getterService = getterService;
    }

    /**
     * processes a webhook for a request event sent by alma
     *
     * @param hook the request webhook
     */
    @Async("threadPoolTaskExecutor")
    public void processRequestHook(RequestHook hook) {
        if (hook.getEvent() == null || hook.getEvent().getValue() == null) {
            log.warn("no hook event type given");
            return;
        }
        HookUserRequest userRequest = hook.getUserRequest();
        log.debug("processing user request from web hook: " + userRequest.toString());
        Item item = this.retrieveItem(userRequest.getItemId());
        if ("WORK_ORDER".equals(userRequest.getRequestType()) && "Int".equals(userRequest.getRequestSubType().getValue())) {
            switch (userRequest.getTargetDestination().getValue()) {
                case "Buchbinder": {
                    String materialType = userRequest.getMaterialType().getValue();
                    if (!("BOOK".equals(materialType) || "ISSBD".equals(materialType))) {
                        log.info("Buchbinder request not for book or bounded issue: " + userRequest.getMaterialType().getValue());
                        break;
                    }

                    // determine library for further temporary locations
                    String library = item.getItemData().getLibrary().getValue();

                    // handle request creation
                    if (HookEventTypes.REQUEST_CREATED.name().equals(hook.getEvent().getValue())) {

                        // attach "wird gebunden" to note
                        this.addPublicNote(item, "in der Einbandstelle");

                        // set temporary location to Buchbinder
                        item.getHoldingData().setInTempLocation(false);
                        // try to create bubi order line
                        try {
                            BubiOrderLine bubiOrderLine = this.bubiOrderLineService.expandBubiOrderLineFromItem(item);
                            log.info(String.format("created new bubi order line | orderlineId: %s, collection: %s, shelfmark: %s", bubiOrderLine.getBubiOrderLineId(), bubiOrderLine.getCollection(), bubiOrderLine.getShelfmark()));
                        } catch (Exception exception) {
                            log.error("could not create bubi order line", exception);
                        }

                        // handle closing of request (return from bubi
                    } else if (HookEventTypes.REQUEST_CLOSED.name().equals(hook.getEvent().getValue())) {
                        // remove "wird gebunden" from note
                        this.removePublicNote(item, "wird gebunden");
                        this.removePublicNote(item, "in der Einbandstelle");
                        // if it is bound issue, set it to the non-publishing temporary location
                        if ("ISSBD".equals(userRequest.getMaterialType().getValue())) {
                            item.getHoldingData().setInTempLocation(true);
                            switch (library) {
                                case "E0001":
                                case "E0023": {
                                    item.getHoldingData().tempLocation(new HoldingDataTempLocation().value("ENP"));
                                    item.getHoldingData().tempLibrary(new HoldingDataTempLibrary().value("E0001"));
                                    break;
                                }
                                case "D0001": {
                                    item.getHoldingData().tempLocation(new HoldingDataTempLocation().value("DNP"));
                                    item.getHoldingData().tempLibrary(new HoldingDataTempLibrary().value("D0001"));
                                    break;
                                }
                            }
                        } else {
                            item.getHoldingData().setInTempLocation(false);
                            item.getHoldingData().tempLocation(null);
                            item.getHoldingData().tempLibrary(null);
                        }
                    }
                    this.almaItemService.updateItem(item);
                    break;
                }
                case "Aussonderung": {
                    log.info("retrieved internal work order for Aussonderung");
                    break;
                }
                case "Umarbeitung": {
                    log.info("retrieved internal work order for Umarbeitung");
                    break;
                }
            }
        } else {
            getterService.indexRequest(hook, item);
        }
    }

    /**
     * processes a webhook for a loan event sent by alma
     *
     * @param hook the loan webhook
     */
    @Async("threadPoolTaskExecutor")
    public void processLoanHook(LoanHook hook) {
        if (hook.getEvent() == null || hook.getEvent().getValue() == null) {
            log.warn("no hook event type given");
            return;
        }

        HookItemLoan itemLoan = hook.getItemLoan();
        log.debug("processing loan from web hook: " + itemLoan.toString());

        AlmaUser almaUser = this.retrieveUser(itemLoan.getUserId());
        Item item = this.retrieveItem(itemLoan.getItemId());
        String mmsId = itemLoan.getMmsId();
        if (HookEventTypes.LOAN_CREATED.name().equals(hook.getEvent().getValue()) || HookEventTypes.LOAN_RETURNED.name().equals(hook.getEvent().getValue())) {
            this.getterService.indexLoan(hook, item, almaUser);
        }
        boolean needScan = false;
        String tempLibrary = "";
        switch (almaUser.getUserGroup().getDesc()) {
            case "Semesterapparat":
                log.info("got sem app loan");
                log.debug(almaUser.getContactInfo().toString());
                for (Address address : almaUser.getContactInfo().getAddress())
                    if (address.getPreferred()) {
                        log.debug(String.format("retrieve item with barcode %s", itemLoan.getItemBarcode()));

                        log.debug(String.format("retrieved item:\n %s", item.toString()));

                        // setting bib data to null in order to avoid problems with network-number / network_numbers....
                        item.setBibData(null);

                        if ("LOAN_CREATED".equals(hook.getEvent().getValue())) {
                            log.debug(String.format("setting public note to %s", address.getLine1()));
                            item.getHoldingData().setInTempLocation(true);
                            this.addPublicNote(item, address.getLine1());
                            String library = itemLoan.getLibrary().getValue();
                            if (address.getLine1().contains("LK") || address.getLine1().contains("BA") || address.getLine1().contains("MC"))
                                library = "D0001";
                            else if (address.getLine1().contains("GW/GSW") || address.getLine1().contains("MNT"))
                                library = "E0001";
                            else if (address.getLine1().contains("Med"))
                                library = "E0023";
                            switch (library) {
                                case "E0001": {
                                    item.getHoldingData().tempLocation(new HoldingDataTempLocation().value("ESA"));
                                    item.getHoldingData().tempLibrary(new HoldingDataTempLibrary().value("E0001"));
                                    break;
                                }
                                case "D0001": {
                                    item.getHoldingData().tempLocation(new HoldingDataTempLocation().value("DSA"));
                                    item.getHoldingData().tempLibrary(new HoldingDataTempLibrary().value("D0001"));
                                    break;
                                }
                                case "E0023": {
                                    item.getHoldingData().tempLocation(new HoldingDataTempLocation().value("MSA"));
                                    item.getHoldingData().tempLibrary(new HoldingDataTempLibrary().value("E0023"));
                                    break;
                                }
                            }
                            log.debug(String.format("retrieved item from library %s", library));
                        } else if ("LOAN_RETURNED".equals(hook.getEvent().getValue())) {
                            log.debug("resetting public note");
                            if (item.getHoldingData().getTempLibrary() != null &&
                                    item.getHoldingData().getTempLibrary().getValue() != null &&
                                    item.getItemData().getLibrary() != null &&
                                    item.getItemData().getLibrary().getValue() != null) {
                                tempLibrary = item.getHoldingData().getTempLibrary().getValue();
                                needScan = !tempLibrary.equals(item.getItemData().getLibrary().getValue());
                            }
                            this.removePublicNote(item, address.getLine1());
                            item.getHoldingData().setInTempLocation(false);
                            item.getHoldingData().tempLocation(null);
                            item.getHoldingData().tempLibrary(null);
                        }
                        log.debug("saving item:\n" + item);
                        this.almaItemService.updateItem(mmsId, item);
                        if (needScan) {
                            waitForAlma(5);
                            this.almaItemService.scanInItemAtLocation(tempLibrary, item);
                        }
                    }
                break;
            case "Neuerw. / 14 Tage":
                log.info("got neuerwerbungs loan");
                log.debug(String.format("retrieve item with barcode %s", itemLoan.getItemBarcode()));
                log.debug(String.format("retrieved item:\n %s", item.toString()));
                // setting bib data to null in order to avoid problems with network-number / network_numbers....
                item.setBibData(null);
                if ("LOAN_CREATED".equals(hook.getEvent().getValue())) {
                    log.debug(String.format("setting public note to %s", almaUser.getLastName()));
                    this.addPublicNote(item,almaUser.getLastName() );
                } else if ("LOAN_RETURNED".equals(hook.getEvent().getValue())) {
                    log.debug("resetting public note");
                    this.removePublicNote(item, almaUser.getLastName());;
                }
                this.almaItemService.updateItem(mmsId, item);
                break;
            default:

        }
    }

    /**
     * processes a webhook for an item event sent by alma
     *
     * @param hook the item webhook
     */
    @Async("threadPoolTaskExecutor")
    public void processItemHook(ItemHook hook) {
        log.debug("processing item hook");
        if (hook.getEvent() == null || hook.getEvent().getValue() == null) {
            log.warn("no hook event type given");
            return;
        }
        log.info(String.format("received item hook with event %s (%s)", hook.getEvent().getDesc(), hook.getEvent().getValue()));
        Item item = hook.getItem();

        log.debug("received item hook: " + item.toString());
        if ("ITEM_DELETED".equals(hook.getEvent().getValue())) {
            this.getterService.deleteItem(item, hook.getTime());
        } else if (HookEventTypes.ITEM_CREATED.name().equals(hook.getEvent().getValue())) {
            this.getterService.index(item, hook.getTime());
        } else if (HookEventTypes.ITEM_UPDATED.name().equals(hook.getEvent().getValue())) {
            this.regalfinderService.checkRegalfinder(item);
            this.getterService.updateItem(item, hook.getTime());
            switch (item.getItemData().getPhysicalMaterialType().getValue()) {
                case "ISSUE": {
                    log.debug(String.format("deleting temporary location for received issue %s for shelfmark %s", item.getItemData().getBarcode(), item.getHoldingData().getCallNumber()));
                    if (!"ACQ".equals(item.getItemData().getProcessType().getValue())) {
                        item.getHoldingData().setInTempLocation(false);
                        item.getHoldingData().tempLocation(null);
                        item.getHoldingData().tempLibrary(null);
                    }
                    break;
                }
                case "ISSBD":
                case "KEYS":
                    break;
                default: {
                    //if (blockedIdService.check(item.getItemData().getPid()))
                    //    return;
                    log.info(String.format("got item with  call number %s and item call number %s", item.getHoldingData().getCallNumber(), item.getItemData().getAlternativeCallNumber()));
                    if (item.getHoldingData().getCallNumber() == null) {
                        log.warn("holding call number is null for item " + item.getItemData().getPid());
                        return;
                    }
                    boolean isChanged = false;
                    if (item.getHoldingData().getInTempLocation())
                        if ("ETA".equals(item.getHoldingData().getTempLocation().getValue()) || "DTR".equals(item.getHoldingData().getTempLocation().getValue())) {
                            if ("LOAN".equals(item.getItemData().getProcessType().getValue())) {
                                item.getHoldingData().setInTempLocation(false);
                                item.getHoldingData().setTempLocation(null);
                                item.getHoldingData().setTempLibrary(null);
                                isChanged = true;
                            }
                        }

                    // check for barcode with blanks
                    String barcode = item.getItemData().getBarcode();
                    if (barcode.contains(" ")) {
                        item.getItemData().setBarcode(item.getItemData().getBarcode().strip());
                        isChanged = true;
                    }
                    // check holding shelfmark
                    String itemCallNo = item.getItemData().getAlternativeCallNumber().strip();
                    if (!itemCallNo.isEmpty()) {
                        // check for call number type if it is not "other" (value 8) set it accordingly
                        ItemDataAlternativeCallNumberType itemDataAlternativeCallNumberType = item.getItemData().getAlternativeCallNumberType();
                        if (itemDataAlternativeCallNumberType == null || itemDataAlternativeCallNumberType.getValue().isEmpty()) {
                            item.getItemData().setAlternativeCallNumberType(new ItemDataAlternativeCallNumberType().value("8"));
                            isChanged = true;
                        }
                        // check whether holding signature needs to be updated
                        String callNo = itemCallNo.replaceAll("\\+\\d+", "");
                        String holdingCallNo = item.getHoldingData().getCallNumber().strip();
                        if (!callNo.equals(holdingCallNo)) {
                            this.almaCatalogService.updateCallNoInHolding(item.getBibData().getMmsId(), item.getHoldingData().getHoldingId(), callNo);
                            isChanged = true;
                        }
                    }
                    if (isChanged)
                        this.almaItemService.updateItem(item);
                }
            }
        }
    }

    /**
     * processes a webhook for a bib event sent by alma
     *
     * @param hook the bib webhook
     */
    @Async("threadPoolTaskExecutor")
    public void processBibHook(BibHook hook) {
        if (hook.getEvent() == null || hook.getEvent().getValue() == null) {
            log.warn("no hook event type given");
            return;
        }
        log.debug("processing bib hook");
        BibWithRecord bib = hook.getBib();
        log.debug("received bib hook: " + bib.toString());
        if ("Universität Duisburg-Essen".equals(bib.getPublisherConst())) {
            String mmsId = bib.getMmsId();
            if (this.almaCatalogService.isPortfolios(mmsId))
                return;

            BibWithRecord bibWithRecord = this.almaCatalogService.getRecord(mmsId);
            boolean isOnline = false;
            boolean isDiss = false;
            String url = "";
            for (MarcDatafield datafield : bibWithRecord.getRecord().getDatafield()) {
                if ("338".equals(datafield.getTag())) {
                    for (MarcSubfield subfield : datafield.getSubfield()) {
                        if ("b".equals(subfield.getCode()))
                            isOnline = "cr".equals(subfield.getValue());
                    }
                } else if ("502".equals(datafield.getTag())) {
                    for (MarcSubfield subfield : datafield.getSubfield()) {
                        if ("b".equals(subfield.getCode()))
                            isDiss = "Dissertation".equals(subfield.getValue());
                    }
                } else if ("856".equals(datafield.getTag())) {
                    for (MarcSubfield subfield : datafield.getSubfield()) {
                        if ("u".equals(subfield.getCode()))
                            url = subfield.getValue();
                    }
                }
            }
            if (isDiss && isOnline) {
                this.almaElectronicService.createDissPortfolio(mmsId, url);
            }
        }
    }

    /**
     * processes a webhook for a bib event sent by alma
     *
     * @param hook the bib webhook
     */
    @Async("threadPoolTaskExecutor")
    public void processJobHook(JobHook hook) {
        log.debug("processing job hook");
        if (hook == null || hook.getJobInstance() == null)
            return;
        String jobName = hook.getJobInstance().getJobInfo().getName();
        if (jobName.contains("EDI - Load Files")) {
            waitForAlma(30);
            String vendorId = jobName.replace("EDI - Load Files", "").strip();
            this.almaInvoiceService.updateEdiInvoices(vendorId);
        }
    }

    private void waitForAlma(int timeout) {
        try {
            log.debug("We wait for a few seconds to give Alma enough time to handle all the updates: collect the item from the basement, carry it to the desk, change the status and bring it back to the basement...");
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException ie) {
            log.warn(String.format("I cannot sleep for %d seconds here...", timeout), ie);
        }
    }

    public void processUserHook(UserHook userHook) {
        log.info("user hook: " + userHook.getId());
    }

    private Item retrieveItem(String itemId) {
        Item item = this.redisService.getItemHook(itemId).getItem();
        if (item == null) {
            log.debug(String.format("did not find item %s in redis cache, collecting user data from alma", itemId));
            return this.almaItemService.findItemByItemId(itemId);
        }
        return item;
    }

    private AlmaUser retrieveUser(String userId) {
        AlmaUser almaUser = this.redisService.getUserHook(userId).getUser();
        if (almaUser == null) {
            log.debug(String.format("did not find user %s in redis cache, collecting user data from alma", userId));
            almaUser = this.almaUserService.getUser(userId);
        }
        return almaUser;
    }

    private void addPublicNote(Item item, String noteText) {
        String note = item.getItemData().getPublicNote();
        if (note == null || note.isEmpty()) {
            item.getItemData().setPublicNote(noteText);
            return;
        }
        if (note.contains(noteText))
            return;
        item.getItemData().setPublicNote(note + "; " + noteText);
    }

    private void removePublicNote(Item item, String noteText) {
        String note = item.getItemData().getPublicNote();
        if (note == null || note.isEmpty()) {
            return;
        }
        if (note.contains(noteText))
            return;
        item.getItemData().setPublicNote(note.replace(noteText, "").trim());

    }
}

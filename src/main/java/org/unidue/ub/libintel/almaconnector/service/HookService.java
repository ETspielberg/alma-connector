package org.unidue.ub.libintel.almaconnector.service;

import lombok.extern.slf4j.Slf4j;
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
     * @param almaInvoiceService the alma invoices api feign client
     * @param regalfinderService the regalfinder service
     * @param redisService the redis cache service
     * @param getterService the elasticsearch connector service
     *
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
    public void processRequestHook(RequestHook hook) {
        // if no event type is given, do not process the hook (it is needed for the decision to be made.
        if (hook.getEvent() == null || hook.getEvent().getValue() == null) {
            log.warn("request hook is not processed - event value is missing");
            return;
        }
        log.info(String.format("processing item hook with event %s (%s)", hook.getEvent().getDesc(), hook.getEvent().getValue()));

        // retrieve the user request object
        HookUserRequest userRequest = hook.getUserRequest();
        log.debug("processing user request from web hook: " + userRequest.toString());

        // retrieve item (preferably from corresponding item hook)
        Item item = this.retrieveItem(userRequest.getItemId());

        // process work orders
        if ("WORK_ORDER".equals(userRequest.getRequestType()) && "Int".equals(userRequest.getRequestSubType().getValue())) {

            // decide upon department of work order (target destination of request)
            switch (userRequest.getTargetDestination().getValue()) {

                // handle buchbinder work orders
                case "Buchbinder": {

                    // check for material type. handle only Book and ISSBD
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
                        this.almaItemService.addPublicNote(item, "in der Einbandstelle");

                        // set temporary location to Buchbinder
                        item.getHoldingData().setInTempLocation(false);

                        // try to create bubi order line
                        try {
                            BubiOrderLine bubiOrderLine = this.bubiOrderLineService.expandBubiOrderLineFromItem(item);
                            log.info(String.format("created new bubi order line | orderlineId: %s, collection: %s, shelfmark: %s", bubiOrderLine.getBubiOrderLineId(), bubiOrderLine.getCollection(), bubiOrderLine.getShelfmark()));
                        } catch (Exception exception) {
                            log.error("could not create bubi order line", exception);
                        }
                    }

                    // handle closing of request (return from bubi
                    else if (HookEventTypes.REQUEST_CLOSED.name().equals(hook.getEvent().getValue())) {

                        // remove '"'wird gebunden' or 'in der Einbandstelle' from note
                        this.almaItemService.removePublicNote(item, "wird gebunden");
                        this.almaItemService.removePublicNote(item, "in der Einbandstelle");

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
                        }
                        // otherwise remove all temporary locations
                        else {
                            item.getHoldingData().setInTempLocation(false);
                            item.getHoldingData().tempLocation(null);
                            item.getHoldingData().tempLibrary(null);
                        }
                    }

                    // update the item in alma
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
        }
        // index 'real' user requests to elasticsearch
        else {
            getterService.indexRequest(hook, item);
        }
    }

    /**
     * processes a webhook for a loan event sent by alma
     *
     * @param hook the loan webhook
     */
    public void processLoanHook(LoanHook hook) {
        // if no event type is given, do not process the hook (it is needed for the decision to be made.
        if (hook.getEvent() == null || hook.getEvent().getValue() == null) {
            log.warn("loan hook is not processed - event value is missing");
            return;
        }
        log.info(String.format("processing item hook with event %s (%s)", hook.getEvent().getDesc(), hook.getEvent().getValue()));

        // retrieve the item loan object
        HookItemLoan itemLoan = hook.getItemLoan();
        log.debug("processing loan from web hook: " + itemLoan.toString());

        // retrieve item and user (preferably from corresponding item and user hook)
        AlmaUser almaUser = this.retrieveUser(itemLoan.getUserId());
        Item item = this.retrieveItem(itemLoan.getItemId());

        // store mms id as bib data are removed later
        String mmsId = itemLoan.getMmsId();

        // index loan event to elasticsearch
        if (HookEventTypes.LOAN_CREATED.name().equals(hook.getEvent().getValue()) || HookEventTypes.LOAN_RETURNED.name().equals(hook.getEvent().getValue())) {
            this.getterService.indexLoan(hook, item, almaUser);
        }

        // initialize the temporary library value
        String tempLibrary = "";

        // prepare boolean for scan, if the item needs to be scanned
        boolean needScan = false;

        // decide upon alma user group
        switch (almaUser.getUserGroup().getDesc()) {
            // handle semapp loans
            case "Semesterapparat":
                log.info("got sem app loan");
                log.debug(almaUser.getContactInfo().toString());
                for (Address address : almaUser.getContactInfo().getAddress())
                    if (address.getPreferred()) {

                        // setting bib data to null in order to avoid problems with network-number / network_numbers....
                        item.setBibData(null);

                        // set temporary location and public note
                        if ("LOAN_CREATED".equals(hook.getEvent().getValue())) {
                            // add public note
                            log.debug(String.format("adding public note '%s'", address.getLine1()));
                            this.almaItemService.addPublicNote(item, address.getLine1());

                            // set temporary location based on library
                            item.getHoldingData().setInTempLocation(true);
                            if (address.getLine1().contains("LK") || address.getLine1().contains("BA") || address.getLine1().contains("MC")) {
                                item.getHoldingData().tempLocation(new HoldingDataTempLocation().value("DSA"));
                                item.getHoldingData().tempLibrary(new HoldingDataTempLibrary().value("D0001"));
                            } else if (address.getLine1().contains("GW/GSW") || address.getLine1().contains("MNT")) {
                                item.getHoldingData().tempLocation(new HoldingDataTempLocation().value("ESA"));
                                item.getHoldingData().tempLibrary(new HoldingDataTempLibrary().value("E0001"));
                            } else if (address.getLine1().contains("Med")) {
                                item.getHoldingData().tempLocation(new HoldingDataTempLocation().value("MSA"));
                                item.getHoldingData().tempLibrary(new HoldingDataTempLibrary().value("E0023"));
                            }
                        }

                        // remove public note and temporary location
                        else if ("LOAN_RETURNED".equals(hook.getEvent().getValue())) {

                            //remove the public note
                            log.debug("resetting public note");
                            this.almaItemService.removePublicNote(item, address.getLine1());

                            // store data for later scan before removing temporary location
                            if (item.getHoldingData().getTempLibrary() != null &&
                                    item.getHoldingData().getTempLibrary().getValue() != null &&
                                    item.getItemData().getLibrary() != null &&
                                    item.getItemData().getLibrary().getValue() != null) {
                                tempLibrary = item.getHoldingData().getTempLibrary().getValue();
                                needScan = !tempLibrary.equals(item.getItemData().getLibrary().getValue());
                            }

                            // remove temporary location
                            item.getHoldingData().setInTempLocation(false);
                            item.getHoldingData().tempLocation(null);
                            item.getHoldingData().tempLibrary(null);
                        }

                        // update item in alma
                        log.debug("saving item:\n" + item);
                        this.almaItemService.updateItem(mmsId, item);

                        // if the item needs to be scanned, wait for alma to porcess everything, then scan the item.
                        if (needScan) {
                            waitForAlma(5);
                            this.almaItemService.scanInItemAtLocation(tempLibrary, item);
                        }
                    }
                break;

            // handling Neuerwerbungsregal
            case "Neuerw. / 14 Tage":
                log.info("got neuerwerbungs loan");

                // setting bib data to null in order to avoid problems with network-number / network_numbers....
                item.setBibData(null);

                // add the user name of the neuerwerbungsregal to the public note
                if ("LOAN_CREATED".equals(hook.getEvent().getValue())) {
                    log.debug(String.format("setting public note to %s", almaUser.getLastName()));
                    this.almaItemService.addPublicNote(item,almaUser.getLastName());
                }

                // remove the user name of the neuerwerbungsregal from the public note
                else if ("LOAN_RETURNED".equals(hook.getEvent().getValue())) {
                    log.debug(String.format("removing '%s' frompublic note", almaUser.getLastName()));
                    this.almaItemService.removePublicNote(item, almaUser.getLastName());
                }

                //update the item in alma
                this.almaItemService.updateItem(mmsId, item);
                break;
            default:
                log.debug("got user loan for "+ almaUser.getUserGroup().getDesc());
        }
    }

    /**
     * processes a webhook for an item event sent by alma
     *
     * @param hook the item webhook
     */
    public void processItemHook(ItemHook hook) {
        // if no event type is given, do not process the hook (it is needed for the decision to be made.
        if (hook.getEvent() == null || hook.getEvent().getValue() == null) {
            log.warn("item hook is not processed - event value is missing");
            return;
        }
        log.info(String.format("processing item hook with event %s (%s)", hook.getEvent().getDesc(), hook.getEvent().getValue()));

        // retrieve item from hook
        Item item = hook.getItem();
        log.debug("received item hook: " + item.toString());

        // index deletion event to elasticsearch
        if ("ITEM_DELETED".equals(hook.getEvent().getValue())) {
            this.getterService.deleteItem(item, hook.getTime());
        }

        // index newly created items to elasticsearch
        else if (HookEventTypes.ITEM_CREATED.name().equals(hook.getEvent().getValue())) {
            this.getterService.index(item, hook.getTime());
        }

        // handle changes in items
        else if (HookEventTypes.ITEM_UPDATED.name().equals(hook.getEvent().getValue())) {

            // check, whether the new shelfmark is found by the regalfinder. an alert is sent via email if it is not.
            this.regalfinderService.checkRegalfinder(item);

            // index the item change to elasticsearch
            this.getterService.updateItem(item, hook.getTime());

            // perform special operations for material types
            switch (item.getItemData().getPhysicalMaterialType().getValue()) {

                // for created issues, remove the temporary locations
                case "ISSUE": {
                    log.debug(String.format("deleting temporary location for received issue %s for shelfmark %s", item.getItemData().getBarcode(), item.getHoldingData().getCallNumber()));
                    if (!"ACQ".equals(item.getItemData().getProcessType().getValue())) {
                        item.getHoldingData().setInTempLocation(false);
                        item.getHoldingData().tempLocation(null);
                        item.getHoldingData().tempLibrary(null);
                    }
                    break;
                }

                // for keys and bound issues, do nothing
                case "ISSBD":
                case "KEYS":
                    break;

                // for all other materials check the item, correct errors, set holding call number and remove temporary locations
                default: {

                    // initalizee boolean indicating whether the item was modified and needs to be updated in alma
                    boolean isChanged = false;

                    // check for barcode with blanks
                    String barcode = item.getItemData().getBarcode();
                    if (barcode.contains(" ")) {
                        item.getItemData().setBarcode(item.getItemData().getBarcode().strip());
                        isChanged = true;
                    }

                    // check for shelfmark with blanks
                    String itemCallNo = item.getItemData().getAlternativeCallNumber();
                    if (itemCallNo.startsWith(" ") || itemCallNo.endsWith(" ")) {
                        itemCallNo = itemCallNo.strip();
                        item.getItemData().setAlternativeCallNumber(itemCallNo);
                        isChanged = true;
                    }

                    log.info(String.format("got item with  call number %s and item call number %s", item.getHoldingData().getCallNumber(), item.getItemData().getAlternativeCallNumber()));
                    if (item.getHoldingData().getCallNumber() != null) {
                        // check holding shelfmark
                        if (!itemCallNo.isEmpty()) {
                            // check for call number type if it is not "other" (value 8) set it accordingly
                            ItemDataAlternativeCallNumberType itemDataAlternativeCallNumberType = item.getItemData().getAlternativeCallNumberType();
                            if (itemDataAlternativeCallNumberType == null || itemDataAlternativeCallNumberType.getValue().isEmpty()) {
                                item.getItemData().setAlternativeCallNumberType(new ItemDataAlternativeCallNumberType().value("8"));
                                isChanged = true;
                            }

                            // retrieve item call number and get base call number by removing item appendix
                            String callNo = itemCallNo.replaceAll("\\+\\d+", "");

                            // retrieve holding call number
                            String holdingCallNo = item.getHoldingData().getCallNumber().strip();

                            // check whether holding signature equals the base call number or if it needs to be updated
                            if (!callNo.equals(holdingCallNo)) {
                                this.almaCatalogService.updateCallNoInHolding(item.getBibData().getMmsId(), item.getHoldingData().getHoldingId(), callNo);
                                isChanged = true;
                            }
                        }
                    }

                    // if the item is in special temporary location, and is changed, remove these temporary locations.
                    if (item.getHoldingData().getInTempLocation()) {
                        if ("ETA".equals(item.getHoldingData().getTempLocation().getValue()) || "DTR".equals(item.getHoldingData().getTempLocation().getValue())) {
                            if ("LOAN".equals(item.getItemData().getProcessType().getValue())) {
                                item.getHoldingData().setInTempLocation(false);
                                item.getHoldingData().setTempLocation(null);
                                item.getHoldingData().setTempLibrary(null);
                                isChanged = true;
                            }
                        }
                    }

                    // if the item was changed, update the item in alma
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
    public void processBibHook(BibHook hook) {
        // if no event type is given, do not process the hook (it is needed for the decision to be made.
        if (hook.getEvent() == null || hook.getEvent().getValue() == null) {
            log.warn("bib hook is not processed - event value is missing");
            return;
        }
        log.info(String.format("processing bib hook with event %s (%s)", hook.getEvent().getDesc(), hook.getEvent().getValue()));

        // get the bibliographic record from the hook
        BibWithRecord bib = hook.getBib();
        log.debug("received bib hook: " + bib.toString());

        // handle publications from the unviersity Duisburg-Essen
        if ("Universität Duisburg-Essen".equals(bib.getPublisherConst())) {

            // get the mms ID
            String mmsId = bib.getMmsId();

            // check the record for existing portfolios. If some are already present, stop the process
            if (this.almaCatalogService.isPortfolios(mmsId))
                return;

            // collect the complete record from alma
            BibWithRecord bibWithRecord = this.almaCatalogService.getRecord(mmsId);

            // initialize the booleans indicating whether it is an online dissertation
            boolean isOnline = false;
            boolean isDiss = false;

            // intilialize the URL
            String url = "";

            // go through all data fields
            for (MarcDatafield datafield : bibWithRecord.getRecord().getDatafield()) {

                // if the field 338 b contains is 'cr' it is an online ressource. set the corresponding boolean
                if ("338".equals(datafield.getTag())) {
                    for (MarcSubfield subfield : datafield.getSubfield()) {
                        if ("b".equals(subfield.getCode()))
                            isOnline = "cr".equals(subfield.getValue());
                    }
                }

                // if the field 502 b cointains 'Dissertation' it is a dissertation. set the corresponding boolean
                else if ("502".equals(datafield.getTag())) {
                    for (MarcSubfield subfield : datafield.getSubfield()) {
                        if ("b".equals(subfield.getCode()))
                            isDiss = "Dissertation".equals(subfield.getValue());
                    }
                }

                // if the field 856 u contains is present, it contains the url to the document. set the variable
                // correspondigly
                else if ("856".equals(datafield.getTag())) {
                    for (MarcSubfield subfield : datafield.getSubfield()) {
                        if ("u".equals(subfield.getCode()))
                            url = subfield.getValue();
                    }
                }
            }

            // if the record describes an online dissertation, create the corresponding portfolios
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
    public void processJobHook(JobHook hook) {
        // if no event type is given, do not process the hook (it is needed for the decision to be made.
        if (hook == null || hook.getJobInstance() == null) {
            log.warn("job hook is not processed - job instances is missing");
            return;
        }
        log.debug("processing job hook");

        // retrieve the job name
        String jobName = hook.getJobInstance().getJobInfo().getName();

        // if an EDI job has finished, update the corresponding invoices.
        if (jobName.contains("EDI - Load Files")) {

            // wait for alma and all processes to have finished
            waitForAlma(30);

            // retrieve the vendorId from the job name
            String vendorId = jobName.replace("EDI - Load Files", "").strip();

            // update the EDI invoices from this vendor.
            this.almaInvoiceService.updateEdiInvoices(vendorId);
        }
    }

    /**
     * just passes some seconds to wait for alma
     * @param timeout the time in seconds to wait
     */
    private void waitForAlma(int timeout) {
        try {
            log.debug("We wait for a few seconds to give Alma enough time to handle all the updates: collect the item from the basement, carry it to the desk, change the status and bring it back to the basement...");
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException ie) {
            log.warn(String.format("I cannot sleep for %d seconds here...", timeout), ie);
        }
    }

    /**
     * processes a webhook for an user event sent by alma
     *
     * @param hook the item webhook
     */
    public void processUserHook(UserHook hook) {
        // if no event type is given, do not process the hook (it is needed for the decision to be made.
        if (hook.getEvent() == null || hook.getEvent().getValue() == null) {
            log.warn("bib hook is not processed - event value is missing");
            return;
        }
        log.info(String.format("processing user hook with event %s (%s)", hook.getEvent().getDesc(), hook.getEvent().getValue()));
    }

    /**
     * retrieves an item preferably from the item web hook in the redis cache. If no web hook can be found, the item is
     * retrieved from the alma API
     * @param itemId the id of the item to be retrieved
     * @return an alma Item object
     */
    private Item retrieveItem(String itemId) {
        // retrieve item from the redis cache
        Item item = this.redisService.getItemHook(itemId).getItem();

        // if the item is not found in the redis cache, collect the item from alma directly
        if (item == null) {
            log.debug(String.format("did not find item %s in redis cache, collecting user data from alma", itemId));
            return this.almaItemService.findItemByItemId(itemId);
        }
        return item;
    }

    /**
     * retrieves an user preferably from the item web hook in the redis cache. If no web hook can be found, the user is
     * retrieved from the alma API
     * @param userId the id of the user to be retrieved
     * @return an AlmaUser object
     */
    private AlmaUser retrieveUser(String userId) {
        // retrieves the user from the redis cache
        AlmaUser almaUser = this.redisService.getUserHook(userId).getUser();

        // if the user is not found in the redis cache, collect the user from alma directly
        if (almaUser == null) {
            log.debug(String.format("did not find user %s in redis cache, collecting user data from alma", userId));
            almaUser = this.almaUserService.getUser(userId);
        }
        return almaUser;
    }
}

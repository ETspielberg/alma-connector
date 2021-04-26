package org.unidue.ub.libintel.almaconnector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.*;
import org.unidue.ub.alma.shared.user.Address;
import org.unidue.ub.alma.shared.user.AlmaUser;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiOrderLine;
import org.unidue.ub.libintel.almaconnector.model.hook.BibHook;
import org.unidue.ub.libintel.almaconnector.model.hook.ItemHook;
import org.unidue.ub.libintel.almaconnector.model.hook.LoanHook;
import org.unidue.ub.libintel.almaconnector.model.hook.RequestHook;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaCatalogService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaElectronicService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaItemService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaUserService;
import org.unidue.ub.libintel.almaconnector.service.bubi.BubiOrderLineService;

@Service
public class HookService {

    private final AlmaUserService almaUserService;

    private final AlmaItemService almaItemService;

    private final AlmaCatalogService almaCatalogService;

    private final AlmaElectronicService almaElectronicService;

    private final BubiOrderLineService bubiOrderLineService;

    private final Logger log = LoggerFactory.getLogger(HookService.class);

    HookService(AlmaUserService almaUserService,
                AlmaItemService almaItemService,
                AlmaCatalogService almaCatalogService,
                BubiOrderLineService bubiOrderLineService,
                AlmaElectronicService almaElectronicService) {
        this.almaUserService = almaUserService;
        this.almaItemService = almaItemService;
        this.almaCatalogService = almaCatalogService;
        this.almaElectronicService = almaElectronicService;
        this.bubiOrderLineService = bubiOrderLineService;
    }

    @Async("threadPoolTaskExecutor")
    public void processRequestHook(RequestHook hook) {
        HookUserRequest userRequest = hook.getUserRequest();
        if ("WORK_ORDER".equals(userRequest.getRequestType()) && "Int".equals(userRequest.getRequestSubType().getValue())) {
            switch (userRequest.getTargetDestination().getValue()) {
                case "Buchbinder": {
                    Item item;
                    if ("BOOK".equals(userRequest.getMaterialType().getValue())) {
                        log.debug(String.format("retrieving barcode %s", userRequest.getBarcode()));
                        item = this.almaItemService.findItemByBarcode(userRequest.getBarcode());
                    } else if ("ISSBD".equals(userRequest.getMaterialType().getValue())) {
                        log.debug(String.format("retrieving mms and item id %s, %s", userRequest.getMmsId(), userRequest.getItemId()));
                        item = this.almaItemService.findItemByMmsAndItemId(userRequest.getMmsId(), userRequest.getItemId());
                    } else
                        break;
                    BubiOrderLine bubiOrderLine = this.bubiOrderLineService.expandBubiOrderLineFromItem(item);
                    this.bubiOrderLineService.saveBubiOrderLine(bubiOrderLine);
                    if ("D0001".equals(item.getItemData().getLibrary().getValue()))
                        item.getHoldingData().tempLocation(new HoldingDataTempLocation().value("DES"));

                    String library = item.getItemData().getLibrary().getValue();
                    item.getHoldingData().setInTempLocation(true);
                    switch (library) {
                        case "E0001":
                        case "E0023": {
                            item.getItemData().setPublicNote("Buchbinder");
                            item.getHoldingData().tempLocation(new HoldingDataTempLocation().value("EBB"));
                            item.getHoldingData().tempLibrary(new HoldingDataTempLibrary().value("E0001"));
                            break;
                        }
                        case "D0001": {
                            item.getItemData().setPublicNote("Einbandstelle");
                            item.getHoldingData().tempLocation(new HoldingDataTempLocation().value("DES"));
                            item.getHoldingData().tempLibrary(new HoldingDataTempLibrary().value("D0001"));
                            break;
                        }
                    }
                    this.almaItemService.updateItem(item);
                    log.info(String.format("created new bubi order line %s for %s: %s", bubiOrderLine.getBubiOrderLineId(), bubiOrderLine.getCollection(), bubiOrderLine.getShelfmark()));
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
    }

    @Async("threadPoolTaskExecutor")
    public void processLoanHook(LoanHook hook) {
        HookItemLoan itemLoan = hook.getItemLoan();
        log.debug(String.format("retrieving user %s", itemLoan.getUserId()));
        AlmaUser almaUser = this.almaUserService.getUser(itemLoan.getUserId());
        log.debug(almaUser.getUserGroup().getDesc());
        switch (almaUser.getUserGroup().getDesc()) {
            case "Semesterapparat":
                log.info("got sem app loan");
                log.debug(almaUser.getContactInfo().toString());
                for (Address address : almaUser.getContactInfo().getAddress())
                    if (address.getPreferred()) {
                        log.debug(String.format("retrieve item with barcode %s", itemLoan.getItemBarcode()));
                        String mmsId = itemLoan.getMmsId();
                        String itemPid = itemLoan.getItemId();
                        Item item = this.almaItemService.findItemByMmsAndItemId(mmsId, itemPid);
                        log.debug(String.format("retrieved item:\n %s", item.toString()));
                        // setting bib data to null in order to avoid problems with network-number / network_numbers....
                        item.setBibData(null);


                        if ("LOAN_CREATED".equals(hook.getEvent().getValue())) {
                            log.debug(String.format("setting public note to %s", address.getLine1()));
                            item.getItemData().setPublicNote(address.getLine1());
                            String library = itemLoan.getLibrary().getValue();
                            item.getHoldingData().setInTempLocation(false);
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
                            item.getItemData().setPublicNote("");
                            item.getHoldingData().setInTempLocation(false);
                            item.getHoldingData().tempLocation(null);
                            item.getHoldingData().tempLibrary(null);
                        }
                        log.debug("saving item:\n" + item);
                        this.almaItemService.updateItem(mmsId, item.getHoldingData().getHoldingId(), itemPid, item);
                    }
                break;
            case "Handapparat":
            case "Handapparat, 15 Ausleihen":
            case "Handapparat, gemeinsamer":
                log.info("got happ loan");
                for (Address address : almaUser.getContactInfo().getAddress())
                    if (address.getPreferred()) {
                        log.debug(String.format("retrieve item with barcode %s", itemLoan.getItemBarcode()));
                        String mmsId = itemLoan.getMmsId();
                        String itemPid = itemLoan.getItemId();
                        Item item = this.almaItemService.findItemByMmsAndItemId(mmsId, itemPid);
                        log.debug(String.format("retrieved item:\n %s", item.toString()));
                        // setting bib data to null in order to avoid problems with network-number / network_numbers....
                        item.setBibData(null);
                        if ("LOAN_CREATED".equals(hook.getEvent().getValue())) {
                            log.debug(String.format("setting public note to %s", address.getLine1()));
                            String library = itemLoan.getLibrary().getValue();
                            item.getHoldingData().setInTempLocation(true);
                            switch (library) {
                                case "E0001": {
                                    item.getHoldingData().tempLocation(new HoldingDataTempLocation().value("EHA"));
                                    item.getHoldingData().tempLibrary(new HoldingDataTempLibrary().value("E0001"));
                                    break;
                                }
                                case "D0001": {
                                    item.getHoldingData().tempLocation(new HoldingDataTempLocation().value("DHA"));
                                    item.getHoldingData().tempLibrary(new HoldingDataTempLibrary().value("D0001"));
                                    break;
                                }
                                case "E0023": {
                                    item.getHoldingData().tempLocation(new HoldingDataTempLocation().value("MHA"));
                                    item.getHoldingData().tempLibrary(new HoldingDataTempLibrary().value("E0023"));
                                    break;
                                }
                            }
                            log.debug(String.format("retrieved item from library %s", library));
                        } else if ("LOAN_RETURNED".equals(hook.getEvent().getValue())) {
                            item.getHoldingData().setInTempLocation(false);
                            item.getHoldingData().tempLocation(null);
                            item.getHoldingData().tempLibrary(null);
                        }
                        log.debug("saving item:\n" + item);
                        this.almaItemService.updateItem(mmsId, item.getHoldingData().getHoldingId(), itemPid, item);
                    }
                break;
            default:
        }
    }

    @Async("threadPoolTaskExecutor")
    public void processItemHook(ItemHook hook) {
        Item item = hook.getItem();
        log.info(String.format("got item with  call number %s and item call number %s", item.getHoldingData().getCallNumber(), item.getItemData().getAlternativeCallNumber()));
        if (item.getHoldingData().getCallNumber() == null) {
            log.warn("holding call number is null for item " + item.getItemData().getPid());
            return;
        }
        if (item.getHoldingData().getCallNumber().strip().isEmpty()) {
            String itemCallNo = hook.getItem().getItemData().getAlternativeCallNumber().strip();
            if (!itemCallNo.isEmpty()) {
                String callNo = itemCallNo.replaceAll("\\+\\d+", "");
                boolean success = this.almaCatalogService.updateCallNoInHolding(item.getBibData().getMmsId(), item.getHoldingData().getHoldingId(), callNo);
                if (success)
                    log.info("successfully updated holding");
            }
        }
    }

    @Async("threadPoolTaskExecutor")
    public void processBibHook(BibHook hook) {
        BibWithRecord bib = hook.getBib();
        if ("Universität Duisburg-Essen".equals(bib.getPublisherConst())) {
            String mmsId = bib.getMmsId();
            BibWithRecord bibWithRecord = this.almaCatalogService.getRecord(mmsId);
            if (this.almaCatalogService.getNumberOfPortfolios(bib.getMmsId()) == 0) {
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
    }
}

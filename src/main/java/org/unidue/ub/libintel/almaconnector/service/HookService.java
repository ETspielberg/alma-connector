package org.unidue.ub.libintel.almaconnector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.*;
import org.unidue.ub.alma.shared.user.Address;
import org.unidue.ub.alma.shared.user.AlmaUser;
import org.unidue.ub.libintel.almaconnector.clients.acquisition.AlmaItemsApiClient;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiOrderLine;
import org.unidue.ub.libintel.almaconnector.model.hook.LoanHook;
import org.unidue.ub.libintel.almaconnector.model.hook.RequestHook;

@Service
public class HookService {

    private final AlmaUserService almaUserService;

    private final AlmaItemsApiClient almaItemsApiClient;

    private final BubiService bubiService;

    private final Logger log = LoggerFactory.getLogger(HookService.class);

    HookService(AlmaUserService almaUserService,
                AlmaItemsApiClient almaItemsApiClient,
                BubiService bubiService) {
        this.almaUserService = almaUserService;
        this.almaItemsApiClient = almaItemsApiClient;
        this.bubiService = bubiService;
    }

    @Async("threadPoolTaskExecutor")
    public void processRequestHook(RequestHook hook) {
        HookUserRequest userRequest = hook.getUserRequest();
        if ("WORK_ORDER".equals(userRequest.getRequestType()) && "Buchbinder".equals(userRequest.getRequestSubType().getValue())) {
            if ("BOOK".equals(userRequest.getMaterialType().getValue())) {
                log.debug(String.format("retrieving barcode %s", userRequest.getBarcode()));
                BubiOrderLine bubiOrderLine = this.bubiService.expandBubiOrderLineFromBarcode(userRequest.getBarcode());
                this.bubiService.saveBubiOrderLine(bubiOrderLine);
                log.info(String.format("created new bubi order line %s for %s: %s", bubiOrderLine.getBubiOrderLineId(), bubiOrderLine.getCollection(), bubiOrderLine.getShelfmark()));
            }
            else if ("ISSBD".equals(userRequest.getMaterialType().getValue())) {
                log.debug(String.format("retrieving mms and item id %s, %s", userRequest.getMmsId(), userRequest.getItemId()));
                BubiOrderLine bubiOrderLine = this.bubiService.expandBubiOrderLineFromMmsAndItemId(userRequest.getMmsId(), userRequest.getItemId());
                this.bubiService.saveBubiOrderLine(bubiOrderLine);
                log.info(String.format("created new bubi order line %s for %s: %s", bubiOrderLine.getBubiOrderLineId(), bubiOrderLine.getCollection(), bubiOrderLine.getShelfmark()));
            }
        }
    }

    @Async("threadPoolTaskExecutor")
    public void processLoanHook(LoanHook hook) {
        HookItemLoan itemLoan = hook.getItemLoan();
        log.debug(String.format("retrieving user %s", itemLoan.getUserId()));
        AlmaUser almaUser = this.almaUserService.getUser(itemLoan.getUserId());
        log.debug(almaUser.getUserGroup().getDesc());
        if ("Semesterapparat".equals(almaUser.getUserGroup().getDesc())) {
            log.info("got sem app loan");
            log.debug(almaUser.getContactInfo().toString());
            for (Address address : almaUser.getContactInfo().getAddress())
                if (address.getPreferred()) {
                    log.debug(String.format("retrieve item with barcode %s", itemLoan.getItemBarcode()));
                    String mmsId = itemLoan.getMmsId();
                    String holdingId = itemLoan.getHoldingId();
                    String itemPid = itemLoan.getItemId();
                    Item item = this.almaItemsApiClient.getItem("application/json", mmsId, holdingId, itemPid);
                    log.debug(String.format("retrieved item:\n %s", item.toString()));
                    // setting bib data to null in order to avoid problems with network-number / network_numbers....
                    item.setBibData(null);


                    if ("LOAN_CREATED".equals(hook.getEvent().getValue())) {
                        log.debug(String.format("setting public note to %s", address.getLine1()));
                        item.getItemData().setPublicNote(address.getLine1());
                        String library = itemLoan.getLibrary().getValue();
                        item.getHoldingData().setInTempLocation(true);
                        switch(library) {
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
                    log.debug("saving item");
                    this.almaItemsApiClient.updateItem("application/json", mmsId, holdingId, itemPid, item);
                }
        }
    }
}

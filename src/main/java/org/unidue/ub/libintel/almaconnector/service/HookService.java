package org.unidue.ub.libintel.almaconnector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.HookItemLoan;
import org.unidue.ub.alma.shared.bibs.Item;
import org.unidue.ub.alma.shared.user.Address;
import org.unidue.ub.alma.shared.user.AlmaUser;
import org.unidue.ub.libintel.almaconnector.clients.acquisition.AlmaItemsApiClient;
import org.unidue.ub.libintel.almaconnector.model.hook.LoanHook;

@Service
public class HookService {

    private final AlmaUserService almaUserService;

    private final AlmaItemsApiClient almaItemsApiClient;

    private final Logger log = LoggerFactory.getLogger(HookService.class);

    HookService(AlmaUserService almaUserService,
                AlmaItemsApiClient almaItemsApiClient) {
        this.almaUserService = almaUserService;
        this.almaItemsApiClient = almaItemsApiClient;
    }

    @Async("threadPoolTaskExecutor")
    public void processLoanHook(LoanHook hook) {
        HookItemLoan itemLoan = hook.getItemLoan();
        log.debug(String.format("retrieving user %s", itemLoan.getUserId()));
        AlmaUser almaUser = this.almaUserService.getUser(itemLoan.getUserId());
        log.debug(almaUser.getUserGroup().getDesc());
        if ("Semesterapparat".equals(almaUser.getUserGroup().getDesc())) {
            log.info("got sem app loan");
            log.info(almaUser.getContactInfo().toString());
            for (Address address : almaUser.getContactInfo().getAddress())
                if (address.getPreferred()) {
                    log.debug(String.format("retrieve item with barcode %s", itemLoan.getItemBarcode()));
                    String mmsId = itemLoan.getMmsId();
                    String holdingId = itemLoan.getHoldingId();
                    String itemPid = itemLoan.getItemId();
                    Item item = this.almaItemsApiClient.getItem("application/json", mmsId, holdingId, itemPid);
                    item.setBibData(null);
                    log.debug(String.format("retrieved item:\n %s", item.toString()));

                    if ("LOAN_CREATED".equals(hook.getEvent().getValue())) {
                        log.debug(String.format("setting public note to %s", address.getLine1()));
                        item.getItemData().setPublicNote(address.getLine1());
                    } else if ("LOAN_RETURNED".equals(hook.getEvent().getValue())) {
                        log.debug("resetting public note to");
                        item.getItemData().setPublicNote("");
                    }
                    log.debug("saving item");
                    this.almaItemsApiClient.updateItem("application/json", mmsId, holdingId, itemPid, item);
                }
        }
    }
}

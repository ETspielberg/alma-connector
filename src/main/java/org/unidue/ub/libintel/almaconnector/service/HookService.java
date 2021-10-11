package org.unidue.ub.libintel.almaconnector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.acq.Invoice;
import org.unidue.ub.alma.shared.acq.InvoiceLine;
import org.unidue.ub.alma.shared.bibs.*;
import org.unidue.ub.alma.shared.user.Address;
import org.unidue.ub.alma.shared.user.AlmaUser;
import org.unidue.ub.libintel.almaconnector.configuration.MappingTables;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrderLine;
import org.unidue.ub.libintel.almaconnector.model.hook.*;
import org.unidue.ub.libintel.almaconnector.service.alma.*;
import org.unidue.ub.libintel.almaconnector.service.bubi.BubiOrderLineService;

import java.util.List;
import java.util.Map;

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

    private final MappingTables mappingTables;

    private final AlmaInvoiceService almaInvoiceService;

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
                MappingTables mappingTables,
                AlmaInvoiceService almaInvoiceService) {
        this.almaUserService = almaUserService;
        this.almaItemService = almaItemService;
        this.almaCatalogService = almaCatalogService;
        this.almaElectronicService = almaElectronicService;
        this.bubiOrderLineService = bubiOrderLineService;
        this.mappingTables = mappingTables;
        this.almaInvoiceService = almaInvoiceService;
    }

    /**
     * processes a webhook for a request event sent by alma
     *
     * @param hook the request webhook
     */
    @Async("threadPoolTaskExecutor")
    public void processRequestHook(RequestHook hook) {
        HookUserRequest userRequest = hook.getUserRequest();
        log.debug("received user request: " + userRequest.toString());
        if ("WORK_ORDER".equals(userRequest.getRequestType()) && "Int".equals(userRequest.getRequestSubType().getValue())) {
            switch (userRequest.getTargetDestination().getValue()) {
                case "Buchbinder": {
                    //first: retrieve item if it is a book or bound issue
                    Item item;
                    if ("BOOK".equals(userRequest.getMaterialType().getValue())) {
                        log.debug(String.format("retrieving barcode %s", userRequest.getBarcode()));
                        item = this.almaItemService.findItemByBarcode(userRequest.getBarcode());
                    } else if ("ISSBD".equals(userRequest.getMaterialType().getValue())) {
                        log.debug(String.format("retrieving mms and item id %s, %s", userRequest.getMmsId(), userRequest.getItemId()));
                        item = this.almaItemService.findItemByMmsAndItemId(userRequest.getMmsId(), userRequest.getItemId());
                    } else {
                        log.info("Buchbinder request not for book or bounded issue: " + userRequest.getMaterialType().getValue());
                        break;
                    }

                    // determine library for further temporary locations
                    String library = item.getItemData().getLibrary().getValue();

                    // handle request creation
                    if (HookEventTypes.REQUEST_CREATED.name().equals(hook.getEvent().getValue())) {

                        // attach "wird gebunden" to note
                        String note = item.getItemData().getPublicNote();
                        if (note == null || note.isEmpty())
                            item.getItemData().setPublicNote("wird gebunden");
                        else
                            item.getItemData().setPublicNote(note + " wird gebunden");

                        // set temporary location to Buchbinder
                        item.getHoldingData().setInTempLocation(false);
                        switch (library) {
                            case "E0001":
                            case "E0023": {
                                item.getHoldingData().tempLocation(new HoldingDataTempLocation().value("EBB"));
                                item.getHoldingData().tempLibrary(new HoldingDataTempLibrary().value("E0001"));
                                break;
                            }
                            case "D0001": {
                                item.getHoldingData().tempLocation(new HoldingDataTempLocation().value("DBB"));
                                item.getHoldingData().tempLibrary(new HoldingDataTempLibrary().value("D0001"));
                                break;
                            }
                        }

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
                        item.getItemData().setPublicNote(item.getItemData().getPublicNote().replace("wird gebunden", "").strip());
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
        }
    }

    /**
     * processes a webhook for a loan event sent by alma
     *
     * @param hook the loan webhook
     */
    @Async("threadPoolTaskExecutor")
    public void processLoanHook(LoanHook hook) {
        HookItemLoan itemLoan = hook.getItemLoan();
        log.debug("received item loan: " + itemLoan.toString());
        log.debug(String.format("retrieving user %s", itemLoan.getUserId()));
        AlmaUser almaUser = this.almaUserService.getUser(itemLoan.getUserId());
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
                            item.getHoldingData().setInTempLocation(true);
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
                        this.almaItemService.updateItem(item);
                    }
                break;
            case "Neuerw. / 14 Tage":
                log.info("got neuerwerbungs loan");
                log.debug(String.format("retrieve item with barcode %s", itemLoan.getItemBarcode()));
                String mmsId = itemLoan.getMmsId();
                String itemPid = itemLoan.getItemId();
                Item item = this.almaItemService.findItemByMmsAndItemId(mmsId, itemPid);
                log.debug(String.format("retrieved item:\n %s", item.toString()));
                // setting bib data to null in order to avoid problems with network-number / network_numbers....
                item.setBibData(null);
                if ("LOAN_CREATED".equals(hook.getEvent().getValue())) {
                    log.debug(String.format("setting public note to %s", almaUser.getLastName()));
                    item.getItemData().setPublicNote(almaUser.getLastName());
                } else if ("LOAN_RETURNED".equals(hook.getEvent().getValue())) {
                    log.debug("resetting public note");
                    item.getItemData().setPublicNote("");
                }
                this.almaItemService.updateItem(item);
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
        if ("ITEM_DELETED".equals(hook.getEvent().getValue()))
            return;
        Item item = hook.getItem();
        log.debug("received item hook: " + item.toString());
        if (hook.getEvent() != null && hook.getEvent().getValue() != null) {
            log.info(String.format("received item hook with event %s (%s)", hook.getEvent().getDesc(), hook.getEvent().getValue()));
        }
        switch (item.getItemData().getPhysicalMaterialType().getValue()) {
            case "ISSUE": {
                log.info(String.format("deleting temporary location for received issue %s for shelfmark %s", item.getItemData().getBarcode(), item.getHoldingData().getCallNumber()));
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
                boolean isItemUpdated = false;
                log.info(String.format("got item with  call number %s and item call number %s", item.getHoldingData().getCallNumber(), item.getItemData().getAlternativeCallNumber()));
                if (item.getHoldingData().getCallNumber() == null) {
                    log.warn("holding call number is null for item " + item.getItemData().getPid());
                    return;
                }

                // check for barcode with blanks
                String barcode = item.getItemData().getBarcode();
                if (barcode.contains(" ")) {
                    item.getItemData().setBarcode(item.getItemData().getBarcode().strip());
                    isItemUpdated = true;
                }
                // check shelfmark
                String itemCallNo = item.getItemData().getAlternativeCallNumber().strip();
                if (!itemCallNo.isEmpty()) {
                    // check for call number type if it is not "other" (value 8) set it accordingly
                    ItemDataAlternativeCallNumberType itemDataAlternativeCallNumberType = item.getItemData().getAlternativeCallNumberType();
                    if (itemDataAlternativeCallNumberType == null || itemDataAlternativeCallNumberType.getValue().isEmpty()) {
                        item.getItemData().setAlternativeCallNumberType(new ItemDataAlternativeCallNumberType().value("8"));
                        isItemUpdated = true;
                    }
                    String callNo = itemCallNo.replaceAll("\\+\\d+", "");
                    String holdingCallNo = item.getHoldingData().getCallNumber().strip();
                    if (!callNo.equals(holdingCallNo))
                        this.almaCatalogService.updateCallNoInHolding(item.getBibData().getMmsId(), item.getHoldingData().getHoldingId(), callNo);
                }
                // update item if changes have been made
                if (isItemUpdated) this.almaItemService.updateItem(item);
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
        String jobName = hook.getJobInstance().getName();
        if (jobName.contains("EDI - Load Files")) {
            String vendorId = jobName.replace("EDI - Load Files", "").strip();
            List<Invoice> invoices = this.almaInvoiceService.getEdiInvoices(vendorId);
            for (Invoice invoice : invoices) {
                String vatCode = invoice.getInvoiceVat().getVatCode().getValue();
                double vatAmount = invoice.getInvoiceVat().getVatAmount();

                invoice.getInvoiceVat().setVatPerInvoiceLine(true);
                for (InvoiceLine invoiceLine : invoice.getInvoiceLines().getInvoiceLine()) {
                    invoiceLine.setPriceNote(invoiceLine.getNote());
                    invoiceLine.setNote("");
                    invoiceLine.getInvoiceLineVat().getVatCode().setValue(vatCode);
                    invoiceLine.getInvoiceLineVat().setVatAmount(vatAmount);
                }
                this.almaInvoiceService.updateInvoice(invoice);
            }
        }

    }


    /**
     * generalized method for processing any kind of hook
     *
     * @param hook the string content of the webhook
     * @param type the typoe of webhook event
     */
    @Async("threadPoolTaskExecutor")
    public void processHook(String hook, String type) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            switch (type) {
                case "loan": {
                    LoanHook loanHook = mapper.readValue(hook, LoanHook.class);
                    processLoanHook(loanHook);
                }
                case "request": {
                    RequestHook requestHook = mapper.readValue(hook, RequestHook.class);
                    processRequestHook(requestHook);
                }
                case "bib": {
                    BibHook bibHook = mapper.readValue(hook, BibHook.class);
                    processBibHook(bibHook);
                }
                case "item": {
                    ItemHook itemHook = mapper.readValue(hook, ItemHook.class);
                    processItemHook(itemHook);
                }
                case "job": {
                    JobHook jobHook = mapper.readValue(hook, JobHook.class);
                    processJobHook(jobHook);
                }
            }
        } catch (Exception e) {
            log.warn("");
        }
    }
}

package org.unidue.ub.libintel.almaconnector.service;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.acq.InterestedUser;
import org.unidue.ub.alma.shared.acq.PoLine;
import org.unidue.ub.alma.shared.bibs.BibWithRecord;
import org.unidue.ub.alma.shared.bibs.Item;
import org.unidue.ub.alma.shared.user.AlmaUser;
import org.unidue.ub.libintel.almaconnector.clients.alma.analytics.AlmaAnalyticsReportClient;
import org.unidue.ub.libintel.almaconnector.configuration.MappingTables;
import org.unidue.ub.libintel.almaconnector.model.analytics.*;
import org.unidue.ub.libintel.almaconnector.model.usage.SingleRequestData;
import org.unidue.ub.libintel.almaconnector.service.alma.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ScheduledService {

    private final AlmaAnalyticsReportClient almaAnalyticsReportClient;

    private final AlmaItemService almaItemService;

    private final AlmaPoLineService almaPoLineService;

    private final AlmaJobsService almaJobsService;

    private final AlmaUserService almaUserService;

    private final AlmaCatalogService almaCatalogService;

    private final MappingTables mappingTables;

    @Value("${libintel.profile:dev}")
    private String profile;

    @Value("${libintel.happ.users}")
    private List<String> happUsers;


    @Value("${libintel.magazin.locations}")
    private List<String> magazinLocations;

    /**
     * constructor based autowiring of the necessary copmponents
     *
     * @param almaAnalyticsReportClient the analytics client to retrieve the reports
     * @param mappingTables             tje mapping tables as defined by the MappingTables configuration and the corresponding configuration file
     * @param almaItemService           the feign client to interact with the alma item API
     * @param almaPoLineService         the feign client to interact with the alma po line API
     * @param almaJobsService           the feign client to interact with the alma jobs API
     */
    ScheduledService(AlmaAnalyticsReportClient almaAnalyticsReportClient,
                     MappingTables mappingTables,
                     AlmaItemService almaItemService,
                     AlmaPoLineService almaPoLineService,
                     AlmaJobsService almaJobsService,
                     AlmaUserService almaUserService,
                     AlmaCatalogService almaCatalogService) {
        this.almaAnalyticsReportClient = almaAnalyticsReportClient;
        this.mappingTables = mappingTables;
        this.almaItemService = almaItemService;
        this.almaPoLineService = almaPoLineService;
        this.almaJobsService = almaJobsService;
        this.almaUserService = almaUserService;
        this.almaCatalogService = almaCatalogService;
    }

    /**
     * updates the item statistics note by the fund code in the corresponding order and the inventory price by the reduced item price
     */
    @Scheduled(cron = "0 0 7 * * *")
    public void updateStatisticField() {
        if (profile.equals("dev")) return;
        // prepare the item statistics notes from the config file
        Map<String, String> codes = mappingTables.getItemStatisticNote();

        // retrieve the analytics report showing all new items of type book with the corresponding order and fund data
        List<NewItemWithOrder> results;
        try {
            results = this.almaAnalyticsReportClient.getReport(NewItemWithFundReport.PATH, NewItemWithFundReport.class).getRows();
        } catch (IOException ioe) {
            log.warn("could not retrieve analytics report.", ioe);
            return;
        }


        // prepare a hashmap to sort the data corresponding to the po line number
        Map<String, List<NewItemWithOrder>> orders = new HashMap<>();
        for (NewItemWithOrder newItemWithOrder : results) {
            String poLineNumber = newItemWithOrder.getPoLineReference();
            if (orders.containsKey(poLineNumber))
                orders.get(poLineNumber).add(newItemWithOrder);
            else {
                List<NewItemWithOrder> newList = new ArrayList<>();
                newList.add(newItemWithOrder);
                orders.put(poLineNumber, newList);
            }
        }

        // go through each po line number
        orders.forEach(
                (polineNumber, list) -> {
                    // retrieve the full po line
                    PoLine poLine = this.almaPoLineService.getPoLine(polineNumber);
                    boolean polineUpdated = false;
                    if (!poLine.getType().getValue().contains("CO")) {
                        for (NewItemWithOrder newItemWithOrder : list) {

                            // go through all connected items
                            boolean itemUpdated = false;

                            // retrieve the full item
                            try {
                                Item item = almaItemService.findItemByMmsAndItemId(newItemWithOrder.getMmsId(), newItemWithOrder.getItemId());
                                try {
                                    double price = Double.parseDouble(poLine.getPrice().getSum());
                                    double discount = Double.parseDouble(poLine.getDiscount());
                                    double reducedPrice = price * (100 - discount) / 100;
                                    String currency = poLine.getPrice().getCurrency().getValue();
                                    if (reducedPrice != 0.0) {
                                        item.getItemData().setInventoryPrice(String.format("%.2f %s", reducedPrice, currency));
                                        itemUpdated = true;
                                        log.debug(String.format("updated item inventory price to %.2f %s", reducedPrice, currency));
                                    }
                                } catch (Exception e) {
                                    log.warn(String.format("could not calculate reduced price because: %s", e.getMessage()));
                                }

                                // try to set the dbs subject statistics field to the value corresponding to the fund code (if there is only one fund used)
                                if (poLine.getFundDistribution().size() == 1) {
                                    String fund = poLine.getFundDistribution().get(0).getFundCode().getValue();
                                    String fundCode = "etat" + fund.substring(0, fund.indexOf("-"));
                                    if (fund.contains("RW"))
                                        fundCode += "RW";
                                    log.info(String.format("updating item price and statistics field for po line %s and fund %s", polineNumber, fund));
                                    if (codes.containsKey(fundCode) && (item.getItemData().getStatisticsNote1() == null || item.getItemData().getStatisticsNote1().isEmpty())) {
                                        item.getItemData().setStatisticsNote1(codes.get(fundCode));
                                        itemUpdated = true;
                                        log.debug(String.format("updated statistics note 1 for item with mms id %s and pid %s to %s",
                                                newItemWithOrder.getMmsId(), newItemWithOrder.getItemId(), codes.get(fund)));
                                    }
                                }

                                if (poLine.getInterestedUser() != null && poLine.getInterestedUser().size() > 0) {
                                    for (InterestedUser interestedUser : poLine.getInterestedUser()) {
                                        String userId = interestedUser.getPrimaryId();
                                        if (userId.equals("CATALOGER") || userId.equals("CD100000091W"))
                                            continue;
                                        try {
                                            AlmaUser almaUser = this.almaUserService.getUser(userId);
                                            if (happUsers.contains(almaUser.getUserGroup().getValue())) {
                                                String receivingNote = poLine.getReceivingNote();
                                                if (receivingNote != null && !receivingNote.isEmpty() && !receivingNote.contains("Happ")) {
                                                    receivingNote += " Happ-Vormerkung";
                                                } else
                                                    receivingNote = "Happ-Vormerkung";
                                                poLine.setReceivingNote(receivingNote);
                                            }
                                        } catch (FeignException fe) {
                                            log.warn(String.format("could not retrieve user %s: ", userId), fe);
                                        }
                                        interestedUser.setNotifyReceivingActivation(false);
                                        interestedUser.setHoldItem(true);
                                        polineUpdated = true;
                                    }
                                }

                                // if the item has been updated, save the changes to alma
                                if (itemUpdated) {
                                    try {
                                        this.almaItemService.updateItem(item);
                                        log.info(String.format("updated item %s with mms id %s", newItemWithOrder.getMmsId(), newItemWithOrder.getItemId()));
                                    } catch (Exception e) {
                                        log.error(String.format("could not update item %s with mms id %s", newItemWithOrder.getMmsId(), newItemWithOrder.getItemId()), e);
                                    }
                                }
                            } catch (FeignException fe) {
                                log.warn(String.format("no item found for mms id %s and item id %s", newItemWithOrder.getMmsId(), newItemWithOrder.getItemId()));
                            }
                        }
                    }
                    if (polineUpdated) {
                        try {
                            this.almaPoLineService.updatePoLine(poLine);
                            log.info(String.format("updated po line %s tu update interested user", polineNumber));
                        } catch (Exception e) {
                            log.error(String.format("could not update po line %s tu update interested user", polineNumber), e);
                        }
                    }
                }
        );
    }

    @Scheduled(cron = "0 0 7,11,15,19 * * 1,2,3,4,5")
    public void runElisaImportDuringWeek() {
        if (profile.equals("dev")) return;
        this.almaJobsService.runElisaImportJob();
    }

    @Scheduled(cron = "0 0 7 * * 6")
    public void runElisaImportAtWeekEnd() {
        if (profile.equals("dev")) return;
        this.almaJobsService.runElisaImportJob();
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void updateBubiOrders() throws IOException {
        if (profile.equals("dev")) return;
        List<OpenBubiOrder> result = this.almaAnalyticsReportClient.getReport(OpenBubiOrdersReport.PATH, OpenBubiOrdersReport.class).getRows();
        for (OpenBubiOrder openBubiOrder : result) {

        }
    }

    @Scheduled(cron = "0 0 5 * * *")
    public void collectRequests() throws IOException {
        List<RequestsItem> result = this.almaAnalyticsReportClient.getReport(RequestsReport.PATH, RequestsReport.class).getRows();
        for (RequestsItem requestsItem : result) {
            switch (requestsItem.getPickupLocation()) {
                case "Campus Duisburg": {
                    requestsItem.setPickupLocation("D0001");
                    break;
                }
                case "FB Medizin": {
                    requestsItem.setPickupLocation("E0023");
                    break;
                }
                default: {
                    requestsItem.setPickupLocation("E0001");
                    break;
                }
            }
            SingleRequestData singleRequestData = new SingleRequestData(requestsItem);
            singleRequestData.setMagazin(magazinLocations.contains(requestsItem.getOwningLocationName()));
            try {
                BibWithRecord bib = this.almaCatalogService.getRecord(requestsItem.getMMSId());
                singleRequestData.setTitle(bib.getTitle());
                singleRequestData.setIsbn(bib.getIsbn());
            } catch (FeignException fe) {
                log.warn("could not retrieve bib data: ", fe);
            }
            try {
                singleRequestData.setNItems(this.almaCatalogService.getNumberOfItems(requestsItem.getMMSId(), requestsItem.getHoldingId()));
            } catch (FeignException fe) {
                log.warn("could not retrieve holding data: ", fe);
            }
            log.info(singleRequestData.toString());
        }
    }
}

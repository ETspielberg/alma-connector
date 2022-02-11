package org.unidue.ub.libintel.almaconnector.service;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.acq.InterestedUser;
import org.unidue.ub.alma.shared.acq.PoLine;
import org.unidue.ub.alma.shared.bibs.BibWithRecord;
import org.unidue.ub.alma.shared.bibs.Item;
import org.unidue.ub.alma.shared.user.AlmaUser;
import org.unidue.ub.libintel.almaconnector.clients.alma.analytics.AlmaAnalyticsReportClient;
import org.unidue.ub.libintel.almaconnector.clients.alma.analytics.AnalyticsNotRetrievedException;
import org.unidue.ub.libintel.almaconnector.configuration.MappingTables;
import org.unidue.ub.libintel.almaconnector.model.analytics.*;
import org.unidue.ub.libintel.almaconnector.model.usage.SingleRequestData;
import org.unidue.ub.libintel.almaconnector.service.alma.*;

import java.util.*;

/**
 * service controlling scheduled tasks.
 */
@Service
@Slf4j
public class ScheduledService {

    private final AlmaAnalyticsReportClient almaAnalyticsReportClient;

    private final AlmaItemService almaItemService;

    private final AlmaPoLineService almaPoLineService;

    private final AlmaJobsService almaJobsService;

    private final AlmaUserService almaUserService;

    private final AlmaCatalogService almaCatalogService;

    private final AlmaSetService almaSetService;

    private final MappingTables mappingTables;

    private final SaveDataService saveDataService;

    private final LogService logService;

    private final CacheManager cacheManager;

    @Value("${libintel.profile:prod}")
    private String profile;

    @Value("${libintel.ub.users.happ}")
    private List<String> happUsers;

    @Value("${libintel.ub.locations.magazin}")
    private List<String> magazinLocations;

    /**
     * constructor based autowiring of the necessary copmponents
     *
     * @param almaAnalyticsReportClient the analytics client to retrieve the reports
     * @param mappingTables             the mapping tables as defined by the MappingTables configuration and the corresponding configuration file
     * @param almaItemService           the alma item service
     * @param almaPoLineService         the alma po line service
     * @param almaJobsService           the alma jobs service
     * @param almaUserService           the alma user service
     * @param almaCatalogService        the alma catalog service
     * @param almaSetService            the alma set service
     * @param saveDataService           the service for saving data
     * @param logService                the log service
     */
    ScheduledService(AlmaAnalyticsReportClient almaAnalyticsReportClient,
                     MappingTables mappingTables,
                     AlmaItemService almaItemService,
                     AlmaPoLineService almaPoLineService,
                     AlmaJobsService almaJobsService,
                     AlmaUserService almaUserService,
                     AlmaCatalogService almaCatalogService,
                     AlmaSetService almaSetService,
                     SaveDataService saveDataService,
                     LogService logService,
                     CacheManager cacheManager) {
        this.almaAnalyticsReportClient = almaAnalyticsReportClient;
        this.mappingTables = mappingTables;
        this.almaItemService = almaItemService;
        this.almaPoLineService = almaPoLineService;
        this.almaJobsService = almaJobsService;
        this.almaUserService = almaUserService;
        this.almaCatalogService = almaCatalogService;
        this.almaSetService = almaSetService;
        this.saveDataService = saveDataService;
        this.logService = logService;
        this.cacheManager = cacheManager;
    }

    /**
     * updates the item statistics note by the fund code in the corresponding order and the inventory price by the reduced item price
     */
    @Scheduled(cron = "0 0 7 * * *")
    public void updateStatisticField() {
        // run only in production mode (analytics reports only available for the prod-system
        if ("dev".equals(profile) || "test".equals(profile)) return;

        // prepare the item statistics notes from the config file
        Map<String, String> codes = mappingTables.getItemStatisticNote();

        // retrieve the analytics report showing all new items of type book with the corresponding order and fund data
        // if the report cannot be retrieved, send error mail.
        List<NewItemWithOrder> results;
        try {
            results = this.almaAnalyticsReportClient.getReport(NewItemWithFundReport.PATH, NewItemWithFundReport.class).getRows();
        } catch (AnalyticsNotRetrievedException analyticsNotRetrievedException) {
            logService.handleAnalyticsException(analyticsNotRetrievedException);
            return;
        }


        // prepare a hashmap to sort the data corresponding to the po line number (PoLineReference)
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

                    // initialize boolean whether the po line was updated
                    boolean polineUpdated = false;

                    // skip ongoing po lines
                    if (!poLine.getType().getValue().contains("CO")) {

                        // go through all coneccted item
                        for (NewItemWithOrder newItemWithOrder : list) {

                            // initialize the boolean indicating whether the item was updated
                            boolean itemUpdated = false;

                            // retrieve the full item
                            try {
                                Item item = almaItemService.findItemByMmsAndItemId(newItemWithOrder.getMmsId(), newItemWithOrder.getItemId());
                                try {
                                    // get the price
                                    double price = Double.parseDouble(poLine.getPrice().getSum());

                                    // get the discount in percent
                                    double discount = Double.parseDouble(poLine.getDiscount());

                                    // calculate the reduced price (price minus discount)
                                    double reducedPrice = price * (100 - discount) / 100;

                                    // get the currency
                                    String currency = poLine.getPrice().getCurrency().getValue();

                                    // if the reduced price has a reasonable value build the price description, set the
                                    // inventory price and mark the item as updated
                                    if (reducedPrice != 0.0) {
                                        String newPrice;
                                        if ("EUR".equals(currency))
                                            newPrice = String.format(Locale.GERMAN, "%.2f", reducedPrice);
                                        else
                                            newPrice = String.format(Locale.GERMAN, "%.2f %s", price, currency);
                                        item.getItemData().setInventoryPrice(newPrice);
                                        itemUpdated = true;
                                        log.debug(String.format("updated item inventory price to %s", newPrice));
                                    }
                                } catch (Exception e) {
                                    log.warn(String.format("could not calculate reduced price because: %s", e.getMessage()));
                                }

                                // try to set the dbs subject statistics field to the value corresponding to the fund code (if there is only one fund used)
                                if (poLine.getFundDistribution().size() == 1) {

                                    // retreive the fund code
                                    String fund = poLine.getFundDistribution().get(0).getFundCode().getValue();

                                    // if it is a reasonable fund an not a sachmittel account
                                    if (fund != null && fund.contains("-")) {

                                        // build the fund code by taking the first block of letters add RW for RW-funds
                                        String fundCode = "etat" + fund.substring(0, fund.indexOf("-"));
                                        if (fund.contains("RW"))
                                            fundCode += "RW";
                                        log.info(String.format("updating item price and statistics field for po line %s and fund %s", polineNumber, fund));

                                        // retrieve the dbs note from the mapping table codes, set it to the statistics
                                        // note 1 of the item and mark the item as updated
                                        if (codes.containsKey(fundCode) && (item.getItemData().getStatisticsNote1() == null || item.getItemData().getStatisticsNote1().isEmpty())) {
                                            item.getItemData().setStatisticsNote1(codes.get(fundCode));
                                            itemUpdated = true;
                                            log.debug(String.format("updated statistics note 1 for item with mms id %s and pid %s to %s",
                                                    newItemWithOrder.getMmsId(), newItemWithOrder.getItemId(), codes.get(fund)));
                                        }
                                    }
                                }

                                // if the poline has interested users, trigger the corresponding actions
                                if (poLine.getInterestedUser() != null && poLine.getInterestedUser().size() > 0) {
                                    for (InterestedUser interestedUser : poLine.getInterestedUser()) {

                                        // retrieve the userID
                                        String userId = interestedUser.getPrimaryId();

                                        // if the user ID is one of the internal ones, stop the further processing
                                        if (userId.equals("CATALOGER") || userId.equals("CD100000091W"))
                                            continue;
                                        try {
                                            // retrieve the alma user
                                            AlmaUser almaUser = this.almaUserService.getUser(userId);

                                            // if the user belongs to the happ-group, set a receiving note to easen the
                                            // processing. Indicate the poline as updated
                                            if (happUsers.contains(almaUser.getUserGroup().getValue())) {
                                                String receivingNote = poLine.getReceivingNote();
                                                if (receivingNote != null && !receivingNote.isEmpty() && !receivingNote.contains("Happ")) {
                                                    receivingNote += " Happ-Vormerkung";
                                                } else
                                                    receivingNote = "Happ-Vormerkung";
                                                poLine.setReceivingNote(receivingNote);
                                                polineUpdated = true;
                                            }
                                        } catch (FeignException fe) {
                                            log.warn(String.format("could not retrieve user %s: ", userId), fe);
                                        }
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

                    // if the poline has been updated, save the changes to alma
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

    /**
     * runs the elisa import job on additional times during the week
     */
    @Scheduled(cron = "0 0 11,15 * * 1,2,3,4,5")
    public void runElisaImportDuringWeek() {
        // do not run this job in the dev environment
        if (profile.equals("dev")) return;
        // trigger the import of elisa data
        this.almaJobsService.runElisaImportJob();
    }

    /**
     * runs the elisa import job on additional times during the week
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void runEndingUserNotificationJob() {
        // do not run this job in the test and dev environment as the analytics report work only in the prod-system
        if ("dev".equals(profile) || "test".equals(profile)) return;
        log.info("updating ending user account set");

        try {
            // empty the set of user ids to be notified and transfer the ids from the analytics report to the
            // corresponding set
            this.almaSetService.transferAusweisAblaufExterneAnalyticsReportToSet();

            // trigger the sending of the emails for the set of users
            this.almaJobsService.runEndingUserNotificationJob();
        } catch (AnalyticsNotRetrievedException analyticsNotRetrievedException) {
            logService.handleAnalyticsException(analyticsNotRetrievedException);
        }
    }

    /**
     * retrieves the open requests and logs the corresponding information to be picked up by beats
     */
    @Scheduled(cron = "0 0 5 * * *")
    public void collectRequests() {
        // do not run this job in the test and dev environment as the analytics report work only in the prod-system
        if ("dev".equals(profile) || "test".equals(profile)) return;

        // prepare the hash map for the requests
        HashMap<String, SingleRequestData> allRequestData = new HashMap<>();

        // retrieve all requested items from the analytics report
        try {
            List<RequestsItem> result = this.almaAnalyticsReportClient.getReport(RequestsReport.PATH, RequestsReport.class).getRows();

            // go through all items
            for (RequestsItem requestsItem : result) {
                // replace the pickup location names by the library codes
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

                // initialize pointer to a SingelRequestData object
                SingleRequestData singleRequestData;

                // generate key from mms-ID, holding-ID and user group
                String key = requestsItem.getMMSId() + "-" + requestsItem.getHoldingId() + "-" + requestsItem.getUserGroup();

                // if there already exists an object in the hash map retrieve this one, otherwise create a new one and store
                // it in the hash map
                if (allRequestData.containsKey(key)) {
                    singleRequestData = allRequestData.get(key);
                } else {
                    singleRequestData = new SingleRequestData(requestsItem);
                    allRequestData.put(key, singleRequestData);
                }

                // add the request depending on the type (magazin, cald or other
                if (magazinLocations.contains(requestsItem.getOwningLocationName()))
                    singleRequestData.addMagazin();
                else if (!requestsItem.getPickupLocation().equals(requestsItem.getOwningLibraryCode()))
                    singleRequestData.addCald();
                else
                    singleRequestData.addRequest();

                // retirive the bib record to get isbn of the requested item and add it ot the SingeRequestData object
                try {
                    BibWithRecord bib = this.almaCatalogService.getRecord(requestsItem.getMMSId());
                    singleRequestData.setTitle(bib.getTitle());
                    singleRequestData.setIsbn(bib.getIsbn());
                } catch (FeignException fe) {
                    log.warn("could not retrieve bib data: ", fe);
                }

                // retrieve the number of items in this location from alma and add it to the SingeRequestData object
                try {
                    singleRequestData.setNItems(this.almaCatalogService.getNumberOfItems(requestsItem.getMMSId(), requestsItem.getHoldingId()));
                } catch (FeignException fe) {
                    log.warn("could not retrieve holding data: ", fe);
                }
            }
            // log all the SingleRequestData in order for beeans to pick them up and store them to elasticsearch
            allRequestData.forEach((key, entry) -> log.info(entry.toString()));
        } catch (AnalyticsNotRetrievedException analyticsNotRetrievedException) {
            logService.handleAnalyticsException(analyticsNotRetrievedException);
        }
    }

    /**
     * retrieves the fine fees from the corresponding analytics report and saves them to the database
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void saveFineFees() {
        // do not run this job in the test and dev environment as the analytics report work only in the prod-system
        if ("dev".equals(profile) || "test".equals(profile)) return;
        try {
            // save the user fines from the daily report to the database
            this.saveDataService.saveDailyUserFineFees();
        } catch (AnalyticsNotRetrievedException analyticsNotRetrievedException) {
            this.logService.handleAnalyticsException(analyticsNotRetrievedException);
        }
    }

    @Scheduled(fixedRate = 5000)
    public void evictAllcachesAtIntervals() {
        cacheManager.getCacheNames()
                .forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
    }

}

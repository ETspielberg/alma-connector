package org.unidue.ub.libintel.almaconnector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.acq.PoLine;
import org.unidue.ub.alma.shared.bibs.Item;
import org.unidue.ub.libintel.almaconnector.clients.analytics.AlmaAnalyticsReportClient;
import org.unidue.ub.libintel.almaconnector.configuration.MappingTables;
import org.unidue.ub.libintel.almaconnector.model.analytics.NewItemWithFundReport;
import org.unidue.ub.libintel.almaconnector.model.analytics.NewItemWithOrderLine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScheduledService {

    private final AlmaAnalyticsReportClient almaAnalyticsReportClient;

    private final ItemService itemService;

    private final AlmaPoLineService almaPoLineService;

    private final AlmaJobsService almaJobsService;

    private final Logger log = LoggerFactory.getLogger(ScheduledService.class);

    private final MappingTables mappingTables;

    ScheduledService(AlmaAnalyticsReportClient almaAnalyticsReportClient,
                     MappingTables mappingTables,
                     ItemService itemService,
                     AlmaPoLineService almaPoLineService,
                     AlmaJobsService almaJobsService) {
        this.almaAnalyticsReportClient = almaAnalyticsReportClient;
        this.mappingTables = mappingTables;
        this.itemService = itemService;
        this.almaPoLineService = almaPoLineService;
        this.almaJobsService = almaJobsService;
    }

    @Scheduled(cron = "0 0 7 * * *")
    public void updateStatisticField() throws IOException {
        Map<String, String> codes = mappingTables.getItemStatisticNote();
        List<NewItemWithOrderLine> results = this.almaAnalyticsReportClient.getReport(NewItemWithOrderLine.PATH, NewItemWithFundReport.class).getRows();
        Map<String, List<NewItemWithOrderLine>> orders = new HashMap<>();
        for (NewItemWithOrderLine newItemWithOrderLine : results) {
            String poLineNumber = newItemWithOrderLine.getPoLineReference();
            if (orders.containsKey(poLineNumber))
                orders.get(poLineNumber).add(newItemWithOrderLine);
            else {
                List<NewItemWithOrderLine> newList = new ArrayList<>();
                newList.add(newItemWithOrderLine);
                orders.put(poLineNumber, newList);
            }
        }
        orders.forEach(
                (polineNumber, list) -> {
                    PoLine poLine = this.almaPoLineService.getPoLine(polineNumber);
                    if (poLine.getFundDistribution().size() == 1) {
                        String fund = poLine.getFundDistribution().get(0).getFundCode().getValue();
                        String fundCode = "etat" + fund.substring(0, fund.indexOf("-"));
                        double reducedPrice = 0.0;
                        String currency = "";
                        try {
                            double price = Double.parseDouble(poLine.getPrice().getSum());
                            double discount = Double.parseDouble(poLine.getDiscount());
                            reducedPrice = price * (1 - discount);
                            currency = poLine.getPrice().getCurrency().getValue();
                        } catch (Exception e) {
                            log.warn("could not calculate reduced price. ", e);
                        }
                        log.info(fund);
                        if (codes.containsKey(fund)) {
                            for (NewItemWithOrderLine newItemWithOrderLine : list) {
                                Item item = itemService.findItemByMmsAndItemId(newItemWithOrderLine.getMmsId(), newItemWithOrderLine.getItemId());
                                if (reducedPrice != 0.0) {
                                    item.getItemData().setInventoryPrice(String.format("%.2f %s", reducedPrice, currency));
                                }
                                if (item.getItemData().getStatisticsNote1() == null || item.getItemData().getStatisticsNote1().isEmpty()) {
                                    item.getItemData().setStatisticsNote1(codes.get(fundCode));
                                    this.itemService.updateItem(item);
                                    log.info(String.format("updated statistics note 1 for item with mms id %s and pid %s to %s",
                                            newItemWithOrderLine.getMmsId(), newItemWithOrderLine.getItemId(), codes.get(fund)));
                                }
                            }
                        }

                    }
                }
        );
    }

    @Scheduled(cron = "0 0 7,11,15,9 * * 1,2,3,4,5")
    public void runElisaImportDuringWeek() {
        this.almaJobsService.runElisaImportJob();
    }

    @Scheduled(cron = "0 0 7 * * 6")
    public void runElisaImportAtWeekEnd() {
        this.almaJobsService.runElisaImportJob();
    }
}

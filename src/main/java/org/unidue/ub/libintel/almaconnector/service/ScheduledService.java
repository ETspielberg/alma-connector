package org.unidue.ub.libintel.almaconnector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.Item;
import org.unidue.ub.libintel.almaconnector.clients.analytics.AlmaAnalyticsReportClient;
import org.unidue.ub.libintel.almaconnector.configuration.MappingTables;
import org.unidue.ub.libintel.almaconnector.model.analytics.NewItemWithFund;
import org.unidue.ub.libintel.almaconnector.model.analytics.NewItemWithFundReport;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ScheduledService {

    private final AlmaAnalyticsReportClient almaAnalyticsReportClient;

    private final ItemService itemService;

    private final Logger log = LoggerFactory.getLogger(ScheduledService.class);

    private final MappingTables mappingTables;

    ScheduledService(AlmaAnalyticsReportClient almaAnalyticsReportClient,
                     MappingTables mappingTables,
                     ItemService itemService) {
        this.almaAnalyticsReportClient = almaAnalyticsReportClient;
        this.mappingTables = mappingTables;
        this.itemService = itemService;
    }

    @Scheduled(cron = "0 0 7 * * *")
    public void updateStatisticField() throws IOException {
        Map<String, String> codes = mappingTables.getItemStatisticNote();
        List<NewItemWithFund> results = this.almaAnalyticsReportClient.getReport(NewItemWithFund.PATH, NewItemWithFundReport.class).getRows();
        for (NewItemWithFund newItemWithFund : results) {
            String fund = newItemWithFund.getFundLedgerCode();
            if (fund.contains("-"))
                fund = "etat" + fund.substring(0, fund.indexOf("-"));
            log.info(String.valueOf(codes.get(fund)));
            if (codes.containsKey(fund)) {
                Item item = itemService.findItemByMmsAndItemId(newItemWithFund.getMmsId(), newItemWithFund.getItemId());
                if (item.getItemData().getStatisticsNote1() == null || item.getItemData().getStatisticsNote1().isEmpty()) {
                    item.getItemData().setStatisticsNote1(codes.get(fund));
                    this.itemService.updateItem(item);
                    log.info(String.format("updated statistics note 1 for item with mms id %s and pid %s to %s",
                            newItemWithFund.getMmsId(), newItemWithFund.getItemId(), codes.get(fund)));
                }
            }

        }
    }
}

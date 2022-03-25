package org.unidue.ub.libintel.almaconnector.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.almaconnector.clients.alma.analytics.AlmaAnalyticsReportClient;
import org.unidue.ub.libintel.almaconnector.clients.alma.analytics.AnalyticsNotRetrievedException;
import org.unidue.ub.libintel.almaconnector.configuration.IdentifierTransferConfiguration;
import org.unidue.ub.libintel.almaconnector.configuration.IdentifierTransferConfigurationMap;
import org.unidue.ub.libintel.almaconnector.model.analytics.*;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaJobsService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaSetService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class IdentifierTransferService {

    private final AlmaSetService almaSetService;

    private final AlmaJobsService almaJobsService;

    private final AlmaAnalyticsReportClient almaAnalyticsReportClient;

    private final IdentifierTransferConfigurationMap identifierTransferConfigurationMap;

    IdentifierTransferService(AlmaSetService almaSetService,
                              AlmaJobsService almaJobsService,
                              AlmaAnalyticsReportClient almaAnalyticsReportClient,
                              IdentifierTransferConfigurationMap identifierTransferConfigurationMap) {
        this.almaSetService = almaSetService;
        this.almaJobsService = almaJobsService;
        this.almaAnalyticsReportClient = almaAnalyticsReportClient;
        this.identifierTransferConfigurationMap = identifierTransferConfigurationMap;
    }

    public void runIdentifierTransport(String name) throws AnalyticsNotRetrievedException {
        log.info("running identifier transfer job " + name);
        IdentifierTransferConfiguration identifierTransferConfiguration = this.identifierTransferConfigurationMap.getIdentifierConfiguration(name);
        this.almaSetService.clearSet(identifierTransferConfiguration.getSetId());
        List<String> ids = new ArrayList<>();
        switch (name) {
            case "offene-gebuehren-mahnung": {
                OffeneGebuehrenMahnungReport offeneGebuehrenMahnungReport = this.almaAnalyticsReportClient.getLongReport(identifierTransferConfiguration.getPath(),OffeneGebuehrenMahnungReport.class, "");
                log.debug("number of rows:" + offeneGebuehrenMahnungReport.getRows().size() + ",  isfinished: " + offeneGebuehrenMahnungReport.isFinished() + ", resumptionToken: " + offeneGebuehrenMahnungReport.getResumptionToken());
                List<OffeneGebuehrenMahnung> offeneGebuehrenMahnungs = new ArrayList<>(offeneGebuehrenMahnungReport.getRows());
                while (!offeneGebuehrenMahnungReport.isFinished() && offeneGebuehrenMahnungReport.getResumptionToken() != null && !offeneGebuehrenMahnungReport.getResumptionToken().isEmpty()) {
                    offeneGebuehrenMahnungReport = this.almaAnalyticsReportClient.getLongReport(OffeneGebuehrenMahnungReport.PATH, OffeneGebuehrenMahnungReport.class, offeneGebuehrenMahnungReport.getResumptionToken());
                    log.debug("number of rows:" + offeneGebuehrenMahnungReport.getRows().size() + ",  isfinished: " + offeneGebuehrenMahnungReport.isFinished() + ", resumptionToken: " + offeneGebuehrenMahnungReport.getResumptionToken());
                    offeneGebuehrenMahnungs.addAll(offeneGebuehrenMahnungReport.getRows());
                }
                for (OffeneGebuehrenMahnung offeneGebuehrenMahnung : offeneGebuehrenMahnungs) {
                    if (offeneGebuehrenMahnung == null || offeneGebuehrenMahnung.getUserId() == null)
                        return;
                    else
                        ids.add(offeneGebuehrenMahnung.getUserId());
                }
                break;
            }
            case "notify-ending": {
                AusweisAblaufExterneReport ausweisAblaufExterneReport = this.almaAnalyticsReportClient.getLongReport(identifierTransferConfiguration.getPath(), AusweisAblaufExterneReport.class, "");
                List<AusweisAblaufExterne> ausweisAblaufExternes = ausweisAblaufExterneReport.getRows();
                if (ausweisAblaufExternes == null)
                    return;
                for (AusweisAblaufExterne ausweisAblaufExterne : ausweisAblaufExternes) {
                    if (ausweisAblaufExterne == null || ausweisAblaufExterne.getIdentifier() == null)
                        return;
                    else
                        ids.add(ausweisAblaufExterne.getIdentifier());
                }
                break;
            }
            default:
                return;
        }
        if (ids.size() == 0)
            return;
        this.almaSetService.addMemberListToSet(identifierTransferConfiguration.getSetId(), ids, "");
        if (identifierTransferConfiguration.getJobId() != null && !identifierTransferConfiguration.getJobId().isEmpty())
            this.almaJobsService.runJob(identifierTransferConfiguration);
    }
}

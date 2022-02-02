package org.unidue.ub.libintel.almaconnector.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.almaconnector.clients.alma.analytics.AlmaAnalyticsReportClient;
import org.unidue.ub.libintel.almaconnector.clients.alma.analytics.AnalyticsNotRetrievedException;
import org.unidue.ub.libintel.almaconnector.model.analytics.*;
import org.unidue.ub.libintel.almaconnector.model.jobs.UserFineFee;
import org.unidue.ub.libintel.almaconnector.repository.jpa.UserFineFeeRepository;

import java.util.List;
import java.util.Optional;

/**
 * saves data for preservation purposes
 */
@Service
@Slf4j
public class SaveDataService {

    private final UserFineFeeRepository userFineFeeRepository;

    private final AlmaAnalyticsReportClient almaAnalyticsReportClient;

    public SaveDataService(UserFineFeeRepository userFineFeeRepository,
                           AlmaAnalyticsReportClient almaAnalyticsReportClient) {
        this.userFineFeeRepository = userFineFeeRepository;
        this.almaAnalyticsReportClient = almaAnalyticsReportClient;
    }

    /**
     *
     * @throws AnalyticsNotRetrievedException thrown, if the analytics report could not be retrieved
     */
    public void saveDailyUserFineFees() throws AnalyticsNotRetrievedException {
            GebuehrenSichernTaeglichReport gebuehrenSichernTaeglichReport = this.almaAnalyticsReportClient.getLongReport(GebuehrenSichernTaeglichReport.PATH, GebuehrenSichernTaeglichReport.class, "");
            log.debug("number of rows: " + gebuehrenSichernTaeglichReport.getRows().size());
            this.saveUserFineFees(gebuehrenSichernTaeglichReport.getRows());
            log.debug("isfinished: " + gebuehrenSichernTaeglichReport.isFinished() + ", resumptionToken: " + gebuehrenSichernTaeglichReport.getResumptionToken());
            while (!gebuehrenSichernTaeglichReport.isFinished() && gebuehrenSichernTaeglichReport.getResumptionToken() != null && !gebuehrenSichernTaeglichReport.getResumptionToken().isEmpty()) {
                gebuehrenSichernTaeglichReport = this.almaAnalyticsReportClient.getLongReport(GebuehrenSichernTaeglichReport.PATH, GebuehrenSichernTaeglichReport.class, gebuehrenSichernTaeglichReport.getResumptionToken());
                log.debug("number of rows: " + gebuehrenSichernTaeglichReport.getRows().size());
                this.saveUserFineFees(gebuehrenSichernTaeglichReport.getRows());
                log.debug("isfinished: " + gebuehrenSichernTaeglichReport.isFinished() + ", resumptionToken: " + gebuehrenSichernTaeglichReport.getResumptionToken());
            }
    }

    public void saveInitialUserFineFees() throws AnalyticsNotRetrievedException {
            GebuehrenSichernReport gebuehrenSichernTaeglichReport = this.almaAnalyticsReportClient.getLongReport(GebuehrenSichernReport.PATH, GebuehrenSichernReport.class, "");
            log.debug("number of rows: " + gebuehrenSichernTaeglichReport.getRows().size());
            this.saveUserFineFees(gebuehrenSichernTaeglichReport.getRows());
            log.debug("isfinished: " + gebuehrenSichernTaeglichReport.isFinished() + ", resumptionToken: " + gebuehrenSichernTaeglichReport.getResumptionToken());
            while (!gebuehrenSichernTaeglichReport.isFinished() && gebuehrenSichernTaeglichReport.getResumptionToken() != null && !gebuehrenSichernTaeglichReport.getResumptionToken().isEmpty()) {
                gebuehrenSichernTaeglichReport = this.almaAnalyticsReportClient.getLongReport(GebuehrenSichernReport.PATH, GebuehrenSichernReport.class, gebuehrenSichernTaeglichReport.getResumptionToken());
                log.debug("number of rows: " + gebuehrenSichernTaeglichReport.getRows().size());
                this.saveUserFineFees(gebuehrenSichernTaeglichReport.getRows());
                log.debug("isfinished: " + gebuehrenSichernTaeglichReport.isFinished() + ", resumptionToken: " + gebuehrenSichernTaeglichReport.getResumptionToken());
            }
    }

    private void saveUserFineFees(List<GebuehrenSichernTaeglich> gebuehrenSichernTaegliches) {
        for (GebuehrenSichernTaeglich gebuehr : gebuehrenSichernTaegliches) {
            Optional<UserFineFee> optionalUserFineFee = this.userFineFeeRepository.findById(gebuehr.getFineFeeId());
            if (optionalUserFineFee.isPresent()) {
                UserFineFee userFineFee  = optionalUserFineFee.get();
                userFineFee.update(gebuehr);
                this.userFineFeeRepository.save(userFineFee);
            } else {
                this.userFineFeeRepository.save(new UserFineFee(gebuehr));
            }
        }
    }
}

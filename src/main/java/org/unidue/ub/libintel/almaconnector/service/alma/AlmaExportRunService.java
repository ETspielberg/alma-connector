package org.unidue.ub.libintel.almaconnector.service.alma;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.almaconnector.model.run.AlmaExportRun;
import org.unidue.ub.libintel.almaconnector.repository.AlmaExportRunRepository;

import java.util.Date;
import java.util.List;

import static org.unidue.ub.libintel.almaconnector.service.SapService.dateformat;

/**
 * The service running the export runs
 *
 * @author Eike Spielberg
 * @author eike.spielberg@uni-due.de
 * @version 1.0
 */
@Service
@Slf4j
public class AlmaExportRunService {

    private final AlmaExportRunRepository almaExportRunRepository;

    /**
     * constructor based autowiring of the repository
     * @param almaExportRunRepository the repository holding the alma export run objects
     */
    AlmaExportRunService(AlmaExportRunRepository almaExportRunRepository) {
        this.almaExportRunRepository = almaExportRunRepository;
    }

    /**
     * checks the existence of alma export run objects and creates a new alma export run object with increased run index.
     * @param date the identifier for the alma export run
     * @return the alma export run object
     */
    public AlmaExportRun getAlmaExportRun(Date date, String owner) {
        log.debug(String.format("collecting AlmaExportRun from database for date %s and owner %s",dateformat.format(date), owner));
        List<AlmaExportRun> savedRuns =  this.almaExportRunRepository.findByIdentifierStartsWithOrderByRunIndex(dateformat.format(date) + "-" + owner);
        if (savedRuns == null || savedRuns.size() == 0) {
            log.debug("no results from alma run repository: " + savedRuns.size());
            return new AlmaExportRun(date).withRunIndex(0).withInvoiceOwner(owner);

        } else {
            log.debug(String.format("found %d results in repositoy.", savedRuns.size()));
            AlmaExportRun almaExportRun = new AlmaExportRun(date).withRunIndex(savedRuns.size()).withInvoiceOwner(owner);
            log.debug(almaExportRun.log());
            return almaExportRun;
        }
    }

    /**
     * saves the alma export run to the database
     * @param almaExportRun the alma export run object
     * @return the saved alma export run object
     */
    public AlmaExportRun saveAlmaExportRun(AlmaExportRun almaExportRun) {
        return this.almaExportRunRepository.save(almaExportRun);
    }
}

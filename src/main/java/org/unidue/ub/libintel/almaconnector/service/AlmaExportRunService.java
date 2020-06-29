package org.unidue.ub.libintel.almaconnector.service;

import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.almaconnector.model.AlmaExportRun;
import org.unidue.ub.libintel.almaconnector.repository.AlmaExportRunRepository;

import java.util.List;

@Service
public class AlmaExportRunService {

    private final AlmaExportRunRepository almaExportRunRepository;

    AlmaExportRunService(AlmaExportRunRepository almaExportRunRepository) {
        this.almaExportRunRepository = almaExportRunRepository;
    }

    public AlmaExportRun getAlmaExportRun(String identifier) {
        List<AlmaExportRun> savedRuns =  this.almaExportRunRepository.findByIdentifierStartsWithOrderByRunIndex(identifier);
        if (savedRuns == null || savedRuns.size() == 1)
            return new AlmaExportRun(identifier).withRunIndex(0);
        else
            return savedRuns.get(0).withRunIndex(savedRuns.size());
    }
}

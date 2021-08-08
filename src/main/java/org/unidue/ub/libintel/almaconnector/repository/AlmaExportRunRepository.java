package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.unidue.ub.libintel.almaconnector.model.run.AlmaExportRun;

import java.util.List;

@Repository
public interface AlmaExportRunRepository extends CrudRepository<AlmaExportRun, String> {

    List<AlmaExportRun> findByIdentifierStartsWithOrderByRunIndex(String identifier);

    AlmaExportRun save(AlmaExportRun almaExportRun);
}

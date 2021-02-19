package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unidue.ub.libintel.almaconnector.model.run.AlmaExportRun;

import java.util.Date;
import java.util.List;

@Repository
public interface AlmaExportRunRepository extends JpaRepository<AlmaExportRun, String> {

    List<AlmaExportRun> findByIdentifierStartsWithOrderByRunIndex(String identifier);

    List<AlmaExportRun> findAllByDesiredDateOrderByRunIndex(Date desiredDate);

    AlmaExportRun save(AlmaExportRun almaExportRun);
}

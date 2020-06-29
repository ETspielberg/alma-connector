package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unidue.ub.libintel.almaconnector.model.AlmaExportRun;

import java.util.List;

@Repository
public interface AlmaExportRunRepository extends JpaRepository<String, AlmaExportRun> {

    List<AlmaExportRun> findByIdentifierStartsWithOrderByRunIndex(String identifier);
}

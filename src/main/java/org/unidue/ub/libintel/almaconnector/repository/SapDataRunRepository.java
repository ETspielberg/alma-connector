package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.unidue.ub.libintel.almaconnector.model.run.SapDataRun;

@Repository
public interface SapDataRunRepository extends CrudRepository<SapDataRun, String> {
}

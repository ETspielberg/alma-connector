package org.unidue.ub.libintel.almaconnector.repository.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.unidue.ub.libintel.almaconnector.model.openaccess.ApcStatistics;

@Repository
public interface ApcStatisticsRepository extends CrudRepository<ApcStatistics, String> {
}

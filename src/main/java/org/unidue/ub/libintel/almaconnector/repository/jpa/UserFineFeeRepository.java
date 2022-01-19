package org.unidue.ub.libintel.almaconnector.repository.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.unidue.ub.libintel.almaconnector.model.jobs.UserFineFee;

@Repository
public interface UserFineFeeRepository extends CrudRepository<UserFineFee, String> {
}

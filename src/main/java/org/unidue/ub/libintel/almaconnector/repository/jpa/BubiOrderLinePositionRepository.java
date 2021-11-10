package org.unidue.ub.libintel.almaconnector.repository.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrderlinePosition;

@Repository
public interface BubiOrderLinePositionRepository extends CrudRepository<BubiOrderlinePosition, String> {

}

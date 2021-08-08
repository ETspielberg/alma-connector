package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrderlinePosition;

@RepositoryRestResource(collectionResourceRel = "bubiOrderlinePosition", path = "bubiOrderlinePosition", exported=false)
public interface BubiOrderLinePositionRepository extends JpaRepository<BubiOrderlinePosition, String> {

}

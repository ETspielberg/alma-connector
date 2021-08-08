package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiPrice;

@RepositoryRestResource(collectionResourceRel = "bubiPrices", path = "bubiPrices")
public interface BubiPricesRepository extends JpaRepository<BubiPrice, Long> {
}

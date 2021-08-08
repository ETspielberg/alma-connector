package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiPrice;

@Repository
public interface BubiPricesRepository extends CrudRepository<BubiPrice, Long> {
}

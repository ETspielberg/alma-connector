package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiPrice;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "bubiPrices", path = "bubiPrices")
public interface BubiPricesRepository extends JpaRepository<BubiPrice, Long> {

    BubiPrice findByNameAndVendorAccount(String name, String vendorAccount);

    void deleteAllByVendorAccount(String vendorAccount);

    List<BubiPrice> findAllByVendorAccount(String vendorAccount);
}

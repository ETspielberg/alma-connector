package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiData;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "bubiData", path = "bubiData")
public interface BubiDataRepository extends JpaRepository<BubiData, String> {

    List<BubiData> findByCampusAndActive(String campus, boolean active);

    List<BubiData> findByActiveOrderByName(boolean active);

    List<BubiData> findByVendorIdAndActive(String vendorId, boolean active);

    BubiData getByVendorAccount(String vendorAccount);
}

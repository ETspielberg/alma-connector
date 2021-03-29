package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiData;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiDataId;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "bubiData", path = "bubiData")
public interface BubiDataRepository extends JpaRepository<BubiData, BubiDataId> {

    List<BubiData> findByCampusAndActive(String campus, boolean active);

    List<BubiData> findByVendorIdAndActive(String vendorId, boolean active);
}

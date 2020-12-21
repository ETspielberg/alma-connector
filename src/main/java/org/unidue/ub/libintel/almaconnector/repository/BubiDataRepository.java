package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiData;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiDataId;

import java.util.List;

public interface BubiDataRepository extends JpaRepository<BubiData, BubiDataId> {

    List<BubiData> findByVendorIdAndCampus(String vendorId, String campus);
}

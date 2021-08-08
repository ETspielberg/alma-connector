package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiData;

import java.util.List;

@Repository
public interface BubiDataRepository extends CrudRepository<BubiData, String> {

    List<BubiData> findByCampusAndActive(String campus, boolean active);

    List<BubiData> findByActiveOrderByName(boolean active);

    List<BubiData> findByVendorIdAndActive(String vendorId, boolean active);

    BubiData getByVendorAccount(String vendorAccount);
}

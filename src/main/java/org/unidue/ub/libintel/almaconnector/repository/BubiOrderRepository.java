package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiOrder;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiStatus;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "bubiOrder", path = "bubiOrder")
public interface BubiOrderRepository  extends JpaRepository<BubiOrder, String> {

    long countAllByVendorId(String vendorId);

    List<BubiOrder> findAllByBubiStatus(BubiStatus BubiStatus);

    long countAllByVendorIdAndVendorAccount(String vendorId, String vendorAccount);
}

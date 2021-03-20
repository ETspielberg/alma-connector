package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiOrderLine;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiStatus;

import java.util.List;
import java.util.UUID;

@RepositoryRestResource(collectionResourceRel = "bubiOrderLine", path = "bubiOrderLine")
public interface BubiOrderLineRepository  extends JpaRepository<BubiOrderLine, UUID> {

    List<BubiOrderLine> findAllByCollectionAndShelfmark(String collection, String shelfmark);

    long countAllByShelfmarkAndCollection(String collection, String shelfmark);

    List<BubiOrderLine> findAllByVendorId(String vendorId);

    List<BubiOrderLine> findAllByVendorIdAndVendorAccount(String vendorId, String vendorAccount);

    BubiOrderLine getBubiOrderLineByAlmaPoLineId(String almaPoLineId);

    BubiOrderLine getBubiOrderLineByBubiOrderLineId(String bubiOrderLineId);

    List<BubiOrderLine> findAllByStatus(BubiStatus status);

}

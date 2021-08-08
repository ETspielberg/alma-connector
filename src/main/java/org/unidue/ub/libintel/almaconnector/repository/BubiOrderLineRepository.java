package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrderLine;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiStatus;

import java.util.List;

@Repository
public interface BubiOrderLineRepository  extends CrudRepository<BubiOrderLine, String> {

    List<BubiOrderLine> findAllByCollectionAndShelfmark(String collection, String shelfmark);

    long countAllByShelfmarkAndCollection(String collection, String shelfmark);

    List<BubiOrderLine> findAllByVendorIdOrderByMinting(String vendorId);

    List<BubiOrderLine> findAllByVendorIdAndVendorAccount(String vendorId, String vendorAccount);

    BubiOrderLine getBubiOrderLineByAlmaPoLineId(String almaPoLineId);

    BubiOrderLine getBubiOrderLineByBubiOrderLineIdOrderByMinting(String bubiOrderLineId);

    List<BubiOrderLine> findAllByStatusOrderByMinting(BubiStatus status);

}

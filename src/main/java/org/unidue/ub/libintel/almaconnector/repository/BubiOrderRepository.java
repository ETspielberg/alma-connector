package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrder;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiStatus;

import java.util.List;

@Repository
public interface BubiOrderRepository  extends CrudRepository<BubiOrder, String> {

    List<BubiOrder> findAllByBubiStatusOrderByBubiOrderId(BubiStatus BubiStatus);

    List<BubiOrder> findAllByMediaTypeOrderByBubiOrderId(String mediaType);

    List<BubiOrder> findAllByMediaTypeAndVendorAccountAndBubiStatusOrderByBubiOrderId(String mediaType, String vendorAccount, BubiStatus bubiStatus);

    long countAllByVendorAccount(String vendorAccount);
}

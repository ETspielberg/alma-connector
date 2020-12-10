package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiOrderLine;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiOrderLineId;

public interface BubiOrderLineRepository  extends JpaRepository<BubiOrderLine, BubiOrderLineId> {
}

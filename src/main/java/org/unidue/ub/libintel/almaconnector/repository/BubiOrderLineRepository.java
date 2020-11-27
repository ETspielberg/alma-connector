package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiOrderLine;

public interface BubiOrderLineRepository  extends JpaRepository<BubiOrderLine, String> {
}

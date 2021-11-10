package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.repository.CrudRepository;
import org.unidue.ub.libintel.almaconnector.model.BlockedId;

public interface BlockedIdRepository extends CrudRepository<BlockedId, String> {
}

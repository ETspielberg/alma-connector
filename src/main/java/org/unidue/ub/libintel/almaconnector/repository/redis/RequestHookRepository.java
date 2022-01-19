package org.unidue.ub.libintel.almaconnector.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.unidue.ub.libintel.almaconnector.model.hook.RequestHook;

@Repository
public interface RequestHookRepository extends CrudRepository<RequestHook, String> {
}

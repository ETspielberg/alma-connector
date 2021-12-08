package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.repository.CrudRepository;
import org.unidue.ub.libintel.almaconnector.model.hook.RequestHook;

public interface RequestHookRepository extends CrudRepository<RequestHook, String> {
}

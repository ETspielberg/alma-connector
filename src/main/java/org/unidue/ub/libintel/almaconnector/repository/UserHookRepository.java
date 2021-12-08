package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.repository.CrudRepository;
import org.unidue.ub.libintel.almaconnector.model.hook.UserHook;

public interface UserHookRepository extends CrudRepository<UserHook, String> {
}

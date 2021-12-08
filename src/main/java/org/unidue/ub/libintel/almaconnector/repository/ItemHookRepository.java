package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.repository.CrudRepository;
import org.unidue.ub.libintel.almaconnector.model.hook.ItemHook;

public interface ItemHookRepository extends CrudRepository<ItemHook, String> {
}

package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.repository.CrudRepository;
import org.unidue.ub.libintel.almaconnector.model.hook.BibHook;

public interface BibHookRepository extends CrudRepository<BibHook, String> {
}
package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.repository.CrudRepository;
import org.unidue.ub.libintel.almaconnector.model.hook.LoanHook;

public interface LoanHookRepository extends CrudRepository<LoanHook, String> {
}

package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.repository.CrudRepository;
import org.unidue.ub.libintel.almaconnector.model.hook.JobHook;

public interface JobHookRepository extends CrudRepository<JobHook, String> {
}

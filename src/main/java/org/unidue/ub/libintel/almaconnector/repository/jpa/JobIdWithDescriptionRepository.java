package org.unidue.ub.libintel.almaconnector.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.unidue.ub.libintel.almaconnector.model.jobs.JobIdWithDescription;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "jobsIdWithDescription", path = "jobsIdWithDescription")
public interface JobIdWithDescriptionRepository extends JpaRepository<JobIdWithDescription, String> {

    List<JobIdWithDescription> findAllByCategoryContainingOrDescriptionContainingOrNameContaining(String category, String description,String name);

}

package org.unidue.ub.libintel.almaconnector.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unidue.ub.libintel.almaconnector.model.jobs.JobIdWithDescription;

import java.util.List;

@Repository
public interface JobIdWithDescriptionRepository extends JpaRepository<JobIdWithDescription, String> {

    List<JobIdWithDescription> findAllByCategoryContainingOrDescriptionContainingOrNameContaining(String category, String description,String name);

}

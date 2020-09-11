package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unidue.ub.libintel.almaconnector.model.run.ShibbolethData;

/**
 * simple repository to manage the creation and retrieval of shibboleth information
 */
@Repository
public interface ShibbolethDataRepository extends JpaRepository<ShibbolethData, String> {

    void deleteByHost(String host);
}

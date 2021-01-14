package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.unidue.ub.libintel.almaconnector.model.bubi.CoreData;

@RepositoryRestResource(collectionResourceRel = "coreData", path = "coreData")
public interface CoreDataRepository  extends JpaRepository<CoreData, String> {

    CoreData findAllByCollectionAndShelfmark(String collection, String shelfmark);
}

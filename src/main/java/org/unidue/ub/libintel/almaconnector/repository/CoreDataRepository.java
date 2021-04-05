package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.unidue.ub.libintel.almaconnector.model.bubi.CoreData;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "coreData", path = "coreData")
public interface CoreDataRepository  extends JpaRepository<CoreData, String> {

    CoreData findAllByCollectionAndShelfmark(String collection, String shelfmark);

    List<CoreData> findAllByAlmaMmsId(String almaMmsId);

    List<CoreData> findAllByActiveOrderByMinting(boolean active);

    CoreData findCoreDataByActiveAndShelfmarkAndMediaTypeOrderByMinting(Boolean active, String shelfmark, String mediaType);
}

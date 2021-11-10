package org.unidue.ub.libintel.almaconnector.repository.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.CoreData;

import java.util.List;

@Repository
public interface CoreDataRepository  extends CrudRepository<CoreData, String> {

    CoreData findAllByCollectionAndShelfmark(String collection, String shelfmark);

    List<CoreData> findAllByActiveOrderByMinting(boolean active);

    CoreData findCoreDataByActiveAndShelfmarkAndMediaTypeOrderByMinting(Boolean active, String shelfmark, String mediaType);
}

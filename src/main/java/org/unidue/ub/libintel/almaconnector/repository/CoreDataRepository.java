package org.unidue.ub.libintel.almaconnector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.unidue.ub.libintel.almaconnector.model.bubi.CoreData;

public interface CoreDataRepository  extends JpaRepository<CoreData, String> {
}

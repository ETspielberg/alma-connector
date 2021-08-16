package org.unidue.ub.libintel.almaconnector.model.bubi.dto;

import lombok.Getter;
import lombok.Setter;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.CoreData;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CoreDataImportRun {

    private long numberOfErrors = 0;

    private List<CoreData> coreDataList = new ArrayList<>();

    public void addCoreData(CoreData coreData) {
        this.coreDataList.add(coreData);
    }
}

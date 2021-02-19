package org.unidue.ub.libintel.almaconnector.model.bubi;

import java.util.ArrayList;
import java.util.List;

public class CoreDataImportRun {

    private long numberOfErrors = 0;

    private List<CoreData> coreDataList = new ArrayList<>();

    public long getNumberOfErrors() {
        return numberOfErrors;
    }

    public void setNumberOfErrors(long numberOfErrors) {
        this.numberOfErrors = numberOfErrors;
    }

    public void increaseNumberOfErrors() {
        this.numberOfErrors++;
    }

    public List<CoreData> getCoreDataList() {
        return coreDataList;
    }

    public void setCoreDataList(List<CoreData> coreDataList) {
        this.coreDataList = coreDataList;
    }

    public void addCoreData(CoreData coreData) {
        this.coreDataList.add(coreData);
    }
}

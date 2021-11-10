package org.unidue.ub.libintel.almaconnector.model.media.elasticsearch;

import java.util.List;

public class Chapter {

    private Long pages;

    private List<Counter> usageCounters;

    public Long getPages() {
        return pages;
    }

    public void setPages(Long pages) {
        this.pages = pages;
    }

    public List<Counter> getUsageCounters() {
        return usageCounters;
    }

    public void setUsageCounters(List<Counter> usageCounters) {
        this.usageCounters = usageCounters;
    }
}

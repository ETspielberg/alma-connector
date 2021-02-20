package org.unidue.ub.libintel.almaconnector.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "libintel.mapping")
public class MappingTables {

    private Map<String, String> itemStatisticNote;

    public Map<String, String> getItemStatisticNote() {
        return itemStatisticNote;
    }

    public void setItemStatisticNote(Map<String, String> itemStatisticNote) {
        this.itemStatisticNote = itemStatisticNote;
    }
}

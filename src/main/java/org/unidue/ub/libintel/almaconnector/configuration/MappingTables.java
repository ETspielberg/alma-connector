package org.unidue.ub.libintel.almaconnector.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * reads in the mapping information to map funds to statistical notes
 */
@Component
@ConfigurationProperties(prefix = "libintel.mapping")
public class MappingTables {

    private Map<String, String> itemStatisticNote;

    /**
     * @return a map with the statistical note for a given fund code
     */
    public Map<String, String> getItemStatisticNote() {
        return itemStatisticNote;
    }

    /**
     * sets the map of statistical notes per fund code
     * @param itemStatisticNote a map object representing the statistical note as string per fund code
     */
    public void setItemStatisticNote(Map<String, String> itemStatisticNote) {
        this.itemStatisticNote = itemStatisticNote;
    }
}

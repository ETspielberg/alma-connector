package org.unidue.ub.libintel.almaconnector.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "libintel.alma.identifier-transfer")
public class IdentifierTransferConfigurationMap {

    private Map<String, IdentifierTransferConfiguration> identifierTransferConfigurationMap = new HashMap<>();

    public Map<String, IdentifierTransferConfiguration> getIdentifierTransferConfigurationMap() {
        return identifierTransferConfigurationMap;
    }

    public void setIdentifierTransferConfigurationMap(Map<String, IdentifierTransferConfiguration> identifierTransferConfigurationMap) {
        this.identifierTransferConfigurationMap = identifierTransferConfigurationMap;
    }

    public IdentifierTransferConfiguration getIdentifierConfiguration(String name) {
        return this.identifierTransferConfigurationMap.get(name);
    }
}

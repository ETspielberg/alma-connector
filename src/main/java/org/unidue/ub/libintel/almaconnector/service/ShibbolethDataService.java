package org.unidue.ub.libintel.almaconnector.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unidue.ub.libintel.almaconnector.model.jobs.ShibbolethData;
import org.unidue.ub.libintel.almaconnector.repository.jpa.ShibbolethDataRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * offers functions around shibboleth data managed in lib intel. These data can be used to build sp-sided or idp-sided WAYFles-URLS
 */
@Service
@Slf4j
public class ShibbolethDataService {

    private final ShibbolethDataRepository shibbolethDataRepository;

    /**
     * constructor based autowiring to the shibboleth data repository
     * @param shibbolethDataRepository the shibboleth data repository
     */
    public ShibbolethDataService(ShibbolethDataRepository shibbolethDataRepository) {
        this.shibbolethDataRepository = shibbolethDataRepository;
    }

    /**
     * saves shibboleth data to the database
     * @param shibbolethData the shibboleth data to be saved
     * @return the saved shibboleth data object
     */
    @Transactional
    public ShibbolethData save(ShibbolethData shibbolethData) {
        return this.shibbolethDataRepository.save(shibbolethData);
    }

    /**
     * retrieves all shibboleth data from teh database
     * @return the list of shibboleth data
     */
    @Transactional
    public List<ShibbolethData> getAllShibbolethData() {
        return new ArrayList<>(this.shibbolethDataRepository.findAll());
    }

    /**
     * retrieves the shibboleth data for a given plattform
     * @param platform the platform the shibboleth data are needed for
     * @return the shibboleth data
     */
    @Transactional
    public ShibbolethData getDataForPlatform(String platform) {
        return this.shibbolethDataRepository.findById(platform).orElse(null);
    }

    /**
     * deletes all shibboleth data for a given platform from the database
     * @param platform the platform the data are to be deleted for
     */
    @Transactional
    public void delete(String platform) {
        this.shibbolethDataRepository.deleteByHost(platform);
    }

}

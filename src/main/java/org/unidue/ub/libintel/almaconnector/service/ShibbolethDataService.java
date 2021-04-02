package org.unidue.ub.libintel.almaconnector.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unidue.ub.libintel.almaconnector.model.jobs.ShibbolethData;
import org.unidue.ub.libintel.almaconnector.repository.ShibbolethDataRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShibbolethDataService {

    private final ShibbolethDataRepository shibbolethDataRepository;

    public ShibbolethDataService(ShibbolethDataRepository shibbolethDataRepository) {
        this.shibbolethDataRepository = shibbolethDataRepository;
    }

    @Transactional
    public ShibbolethData save(ShibbolethData shibbolethData) {
        return this.shibbolethDataRepository.save(shibbolethData);
    }

    @Transactional
    public List<ShibbolethData> getAllShibbolethData() {
        List<ShibbolethData> shibbolethDataList = new ArrayList<>();
        this.shibbolethDataRepository.findAll().forEach(shibbolethDataList::add);
        return shibbolethDataList;
    }

    @Transactional
    public ShibbolethData getDataForPlatform(String platform) {
        return this.shibbolethDataRepository.findById(platform).orElse(null);
    }

    @Transactional
    public void delete(String platform) {
        this.shibbolethDataRepository.deleteByHost(platform);
    }

}

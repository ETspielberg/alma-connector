package org.unidue.ub.libintel.almaconnector.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.conf.Job;
import org.unidue.ub.libintel.almaconnector.clients.conf.AlmaJobsApiClient;

@Service
public class AlmaJobsService {

    private final AlmaJobsApiClient almaJobsApiClient;

    @Value("${alma.elisa.import.job.id}")
    private String elisaJobId;

    public AlmaJobsService(AlmaJobsApiClient almaJobsApiClient) {
        this.almaJobsApiClient = almaJobsApiClient;
    }

    public void runElisaImportJob() {
        this.almaJobsApiClient.postAlmawsV1ConfJobsJobId(new Job(),elisaJobId, "run");

    }
}

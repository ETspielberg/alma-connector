package org.unidue.ub.libintel.almaconnector.service.alma;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.conf.Job;
import org.unidue.ub.alma.shared.conf.Jobs;
import org.unidue.ub.libintel.almaconnector.clients.conf.AlmaJobsApiClient;
import org.unidue.ub.libintel.almaconnector.model.jobs.JobIdWithDescription;
import org.unidue.ub.libintel.almaconnector.repository.JobIdWithDescriptionRepository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AlmaJobsService {

    private final AlmaJobsApiClient almaJobsApiClient;

    private final JobIdWithDescriptionRepository jobIdWithDescriptionRepository;

    private final EntityManager entityManager;

    @Value("${alma.elisa.import.job.id:00000000}")
    private String elisaJobId;

    public AlmaJobsService(AlmaJobsApiClient almaJobsApiClient,
                           JobIdWithDescriptionRepository jobIdWithDescriptionRepository,
                           EntityManager entityManager) {
        this.almaJobsApiClient = almaJobsApiClient;
        this.jobIdWithDescriptionRepository = jobIdWithDescriptionRepository;
        this.entityManager = entityManager;
    }

    public void runElisaImportJob() {
        this.almaJobsApiClient.postAlmawsV1ConfJobsJobId(new Job(),elisaJobId, "run");

    }

    public void updateJobsList() {
        Integer limit = 100;
        Integer offset = 0;
        Jobs jobs = this.almaJobsApiClient.getAlmawsV1ConfJobs("application/json", limit, offset, "", "", "");
        List<Job> allJobs = new ArrayList<>(jobs.getJob());
        int totalNumberOfJobs = jobs.getTotalRecordCount();
        while (allJobs.size() < totalNumberOfJobs) {
            offset += limit;
            Jobs jobsInd = this.almaJobsApiClient.getAlmawsV1ConfJobs("application/json", limit, offset, "", "", "");
            allJobs.addAll(jobsInd.getJob());
        }
        if (allJobs.size() > 0) {
            // String sql = "TRUNCATE his_export;";
            // entityManager.createNativeQuery(sql).executeUpdate();
            for (Job job: allJobs) {
                JobIdWithDescription jobIdWithDescription = new JobIdWithDescription(job.getId())
                        .withDescription(job.getDescription())
                        .withName(job.getName())
                        .withCategory(job.getCategory().getValue());
                jobIdWithDescriptionRepository.save(jobIdWithDescription);
            }
        }
    }

    public List<JobIdWithDescription> searchJob(String term) {
        return this.jobIdWithDescriptionRepository.findAllByCategoryContainingOrDescriptionContainingOrNameContaining(term, term, term);
    }
}

package org.unidue.ub.libintel.almaconnector.service.alma;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.conf.Job;
import org.unidue.ub.alma.shared.conf.Jobs;
import org.unidue.ub.libintel.almaconnector.clients.alma.conf.AlmaJobsApiClient;
import org.unidue.ub.libintel.almaconnector.model.JobParametersFile;
import org.unidue.ub.libintel.almaconnector.model.jobs.JobIdWithDescription;
import org.unidue.ub.libintel.almaconnector.repository.jpa.JobIdWithDescriptionRepository;
import org.unidue.ub.libintel.almaconnector.service.XmlReaderService;

import java.util.ArrayList;
import java.util.List;

/**
 * offers functions around jobs in Alma
 *
 * @author Eike Spielberg
 * @author eike.spielberg@uni-due.de
 * @version 1.0
 */
@Service
@Slf4j
public class AlmaJobsService {

    private final AlmaJobsApiClient almaJobsApiClient;

    private final JobIdWithDescriptionRepository jobIdWithDescriptionRepository;

    private final XmlReaderService xmlReaderService;

    @Value("${alma.elisa.import.job.id:00000000}")
    private String elisaJobId;

    @Value("${alma.notify.ending.job.id:00000001}")
    private String notifyEndingJobId;

    /**
     * constructor based autowiring of the alma jobs api feign client and the jobs with description repository
     * @param almaJobsApiClient the alma jobs api feign client
     * @param jobIdWithDescriptionRepository the jobs with description repository
     */
    public AlmaJobsService(AlmaJobsApiClient almaJobsApiClient,
                           JobIdWithDescriptionRepository jobIdWithDescriptionRepository,
                           XmlReaderService xmlReaderService) {
        this.almaJobsApiClient = almaJobsApiClient;
        this.jobIdWithDescriptionRepository = jobIdWithDescriptionRepository;
        this.xmlReaderService = xmlReaderService;
    }

    /**
     * runs the elisa import job (elisa job id given as configuration parameter 'alma.elisa.import.job.id'
     */
    public void runElisaImportJob() {
        this.almaJobsApiClient.postAlmawsV1ConfJobsJobId(new JobParametersFile(),elisaJobId, "run");

    }

    /**
     * generates a list of <class>JobIdWithDescription</class> objects and stores them in the database
     */
    public void updateJobsList() {
        int limit = 100;
        int offset = 0;
        Jobs jobs = this.almaJobsApiClient.getAlmawsV1ConfJobs("application/json", limit, offset, "", "", "");
        List<Job> allJobs = new ArrayList<>(jobs.getJob());
        int totalNumberOfJobs = jobs.getTotalRecordCount();
        while (allJobs.size() < totalNumberOfJobs) {
            offset += limit;
            Jobs jobsInd = this.almaJobsApiClient.getAlmawsV1ConfJobs("application/json", limit, offset, "", "", "");
            allJobs.addAll(jobsInd.getJob());
        }
        if (allJobs.size() > 0) {
            for (Job job: allJobs) {
                JobIdWithDescription jobIdWithDescription = new JobIdWithDescription(job.getId())
                        .withDescription(job.getDescription())
                        .withName(job.getName())
                        .withCategory(job.getCategory().getValue());
                jobIdWithDescriptionRepository.save(jobIdWithDescription);
            }
        }
    }

    /**
     * searches the jobs with description repository for a given search term
     * @param term the search term
     * @return a list of <class>JobIdWithDescription</class> objects
     */
    public List<JobIdWithDescription> searchJob(String term) {
        return this.jobIdWithDescriptionRepository.findAllByCategoryContainingOrDescriptionContainingOrNameContaining(term, term, term);
    }

    public void runEndingUserNotificationJob() {
        JobParametersFile job = this.xmlReaderService.readJobParameters("BenutzerAusweisende");
        log.info(String.format("running jo %s with parameters %s", notifyEndingJobId, job.toString()));
        try {
            this.almaJobsApiClient.postAlmawsV1ConfJobsJobId(job, notifyEndingJobId, "run");
        } catch (FeignException feignException) {
            log.warn(String.format("could not start job %s: %s", notifyEndingJobId, feignException.getMessage()), feignException);
        }
    }
}

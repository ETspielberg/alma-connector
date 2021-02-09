package org.unidue.ub.libintel.almaconnector.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.conf.Job;
import org.unidue.ub.alma.shared.conf.JobInstance;

@Service
public class JobLoggerService {

    private final ObjectMapper objectMapper;

    private final Logger log = LoggerFactory.getLogger(JobLoggerService.class);

    JobLoggerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void logJob(JobInstance jobInstance) {
        try {
            log.info(objectMapper.writeValueAsString(jobInstance));
        } catch (JsonProcessingException e) {
            log.error("could not write job");
        }
    }
}

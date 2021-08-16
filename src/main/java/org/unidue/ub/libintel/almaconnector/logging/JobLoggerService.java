package org.unidue.ub.libintel.almaconnector.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.conf.JobInstance;

@Service
@Slf4j
public class JobLoggerService {

    private final ObjectMapper objectMapper;

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

package org.unidue.ub.libintel.almaconnector.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.almaconnector.model.hook.*;
import org.unidue.ub.libintel.almaconnector.model.run.SapDataRun;
import org.unidue.ub.libintel.almaconnector.repository.redis.*;

@Service
@Slf4j
public class RedisService {

    private final JobHookRepository jobHookRepository;

    private final ItemHookRepository itemHookRepository;

    private final UserHookRepository userHookRepository;

    private final LoanHookRepository loanHookRepository;

    private final RequestHookRepository requestHookRepository;

    private final BibHookRepository bibHookRepository;

    private final RedisTemplate<String, SapDataRun> sapDataRunRedisTemplate;

    public RedisService(JobHookRepository jobHookRepository,
                        ItemHookRepository itemHookRepository,
                        UserHookRepository userHookRepository,
                        LoanHookRepository loanHookRepository,
                        RequestHookRepository requestHookRepository,
                        BibHookRepository bibHookRepository,
                        RedisTemplate<String, SapDataRun> redisSapDataRunTemplate) {
        this.jobHookRepository = jobHookRepository;
        this.itemHookRepository = itemHookRepository;
        this.bibHookRepository = bibHookRepository;
        this.loanHookRepository = loanHookRepository;
        this.requestHookRepository = requestHookRepository;
        this.userHookRepository = userHookRepository;
        this.sapDataRunRedisTemplate = redisSapDataRunTemplate;
    }

    public JobHook getJobHook(String id) {
        return this.jobHookRepository.findById(id).orElse(null);
    }

    public ItemHook getItemHook(String id) {
        return this.itemHookRepository.findById(id).orElse(null);
    }

    public BibHook getBibHook(String id) {
        return this.bibHookRepository.findById(id).orElse(null);
    }

    public LoanHook getLoanHook(String id) {
        return this.loanHookRepository.findById(id).orElse(null);
    }

    public RequestHook getRequestHook(String id) {
        return this.requestHookRepository.findById(id).orElse(null);
    }

    public UserHook getUserHook(String id) {
        return this.userHookRepository.findById(id).orElse(null);
    }

    public ItemHook getExpiredItemHook(String id) {
        return this.getItemHook(getPhantomId(id));
    }

    public BibHook getExpiredBibHook(String id) {
        return this.getBibHook(getPhantomId(id));
    }

    public JobHook getExpiredJobHook(String id) {
        return this.getJobHook(getPhantomId(id));
    }

    public LoanHook getExpiredLoanHook(String id) {
        return this.getLoanHook(getPhantomId(id));
    }

    public RequestHook getExpiredRequestHook(String id) {
        return this.getRequestHook(getPhantomId(id));
    }

    public UserHook getExpiredUserHook(String id) {
        return this.getUserHook(getPhantomId(id));
    }

    /**
     * generalized method for processing any kind of hook
     *
     * @param hook the string content of the webhook
     * @param type the typoe of webhook event
     */
    public void cacheHook(String hook, String type) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            log.debug(hook);
            switch (type) {
                case "loan": {
                    LoanHook loanHook = mapper.readValue(hook, LoanHook.class);
                    log.info(String.format("revceived hook of type %s and event %s", loanHook.getAction(), loanHook.getEvent().getValue()));
                    this.loanHookRepository.save(loanHook);
                    break;
                }
                case "request": {
                    RequestHook requestHook = mapper.readValue(hook, RequestHook.class);
                    log.info(String.format("revceived hook of type %s and event %s", requestHook.getAction(), requestHook.getEvent().getValue()));
                    this.requestHookRepository.save(requestHook);
                    break;
                }
                case "bib": {
                    BibHook bibHook = mapper.readValue(hook, BibHook.class);
                    log.info(String.format("revceived hook of type %s and event %s", bibHook.getAction(), bibHook.getEvent().getValue()));
                    this.bibHookRepository.save(bibHook);
                    break;
                }
                case "item": {
                    ItemHook itemHook = mapper.readValue(hook, ItemHook.class);
                    log.info(String.format("revceived hook of type %s and event %s", itemHook.getAction(), itemHook.getEvent().getValue()));
                    this.itemHookRepository.save(itemHook);
                    break;
                }
                case "job": {
                    JobHook jobHook = mapper.readValue(hook, JobHook.class);
                    this.jobHookRepository.save(jobHook);
                    log.info(String.format("cached job hook of type %s", jobHook.getAction()));
                    break;
                }
                case "user": {
                    UserHook userHook = mapper.readValue(hook, UserHook.class);
                    this.userHookRepository.save(userHook);
                    log.info(String.format("cached job hook of type %s", userHook.getAction()));
                    break;
                }
            }
        } catch (Exception e) {
            log.warn("", e);
        }
    }

    private String getPhantomId(String id) {
        return id + ":phantom";
    }

    public SapDataRun cache(SapDataRun sapDataRun) {
        this.sapDataRunRedisTemplate.opsForValue().set(sapDataRun.getIdentifier(), sapDataRun);
        return sapDataRun;
    }

    public SapDataRun retrieveAlmaExportRun(String invoiceOwner, long counter) {
        String id = String.format("%s-%s", invoiceOwner, counter);
        SapDataRun sapDataRun = sapDataRunRedisTemplate.opsForValue().get(id);
        if (sapDataRun != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                log.debug("retrieved sap data run from redis cache: \n" + objectMapper.writeValueAsString(sapDataRun));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return sapDataRun;
        } else {
            log.debug("did not find sap data run in redis cache");
            return new SapDataRun(invoiceOwner).withRunIndex(counter);
        }
    }
}

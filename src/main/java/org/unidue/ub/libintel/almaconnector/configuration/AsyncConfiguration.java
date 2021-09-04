package org.unidue.ub.libintel.almaconnector.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * configures the executor for the asynchronous tasks
 */
@Configuration
@EnableAsync
public class AsyncConfiguration {

    /**
     * configures a simple thred pool task executor
     * @return the excutor for the asynchronous tasks
     */
    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        return new ThreadPoolTaskExecutor();
    }
}

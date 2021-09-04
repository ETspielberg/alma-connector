package org.unidue.ub.libintel.almaconnector.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.concurrent.DelegatingSecurityContextScheduledExecutorService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * configures the scheduled tasks
 */
@Configuration
public class SchedulingConfiguration implements SchedulingConfigurer {

    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    /**
     * defines the task executor for the scheduled tasks
     * @param taskRegistrar the task registrar bean
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
    }

    /**
     * creates the task executor bean for the scheduled tasks. adds the context object to allow for accessing other
     * services with the system password provided as environment variables
     * @return the task executor bean
     */
    @Bean
    public Executor taskExecutor() {
        ScheduledExecutorService delegateExecutor = Executors.newSingleThreadScheduledExecutor();
        SecurityContext schedulerContext = createSchedulerSecurityContext();
        return new DelegatingSecurityContextScheduledExecutorService(delegateExecutor, schedulerContext);
    }

    private SecurityContext createSchedulerSecurityContext() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_SYSTEM");
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                username,
                password,
                authorities
        );
        context.setAuthentication(authentication);
        return context;
    }
}

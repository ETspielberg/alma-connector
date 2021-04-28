package org.unidue.ub.libintel.almaconnector.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * Configuration of web security.
 */
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${libintel.alma.hook.secret:test}")
    private String secret;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
                .cors().disable()
                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).ignoringAntMatchers("/hooks/**", "/invoicesUpdate").and()
                .authorizeRequests()
                .antMatchers("/start", "/hooks/**")
                .permitAll()
                .anyRequest().authenticated();
    }

    /*@Bean
    public FilterRegistrationBean<HookSignatureFilter> loggingFilter(){
        FilterRegistrationBean<HookSignatureFilter> registrationBean
                = new FilterRegistrationBean<>();
        registrationBean.setFilter(new HookSignatureFilter().withSecret(this.secret));
        registrationBean.addUrlPatterns("/hooks/*");
        return registrationBean;
    }
    */
}

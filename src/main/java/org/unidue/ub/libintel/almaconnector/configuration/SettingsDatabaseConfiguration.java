package org.unidue.ub.libintel.almaconnector.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * Data base configuration for storing the individual collection run parameters
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {"org.unidue.ub.libintel.almaconnector.repository.jpa"}
)
public class SettingsDatabaseConfiguration {

    /**
     * registers the datasource bean for the settings repository
     * @return the datasource bean
     */
    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.settings")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * registers the local entity manager factory bean
     * @param builder the factory builder for entity managers
     * @param dataSource the datasource bean
     * @return the entity manager factory bean
     */
    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean
    entityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier("dataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("org.unidue.ub.libintel.almaconnector.model.run","org.unidue.ub.libintel.almaconnector.model.bubi", "org.unidue.ub.libintel.almaconnector.model.jobs", "org.unidue.ub.libintel.almaconnector.model.openaccess")
                .build();
    }

    /**
     * registers the transaction manager bean
     * @param entityManagerFactory the the local entity manager factory bean
     * @return the platform transaction manager
     */
    @Primary
    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}

package org.unidue.ub.libintel.almaconnector.configuration;

import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration of the cache used in API calls.
 */
@Configuration
public class CacheConfiguration {

    /**
     * customizes the cache manager to be used for retrieving the vendor accounts
     * @return CacheManagerCustomizer
     */
    @Bean
    public CacheManagerCustomizer<ConcurrentMapCacheManager> cacheManagerCustomizer() {
        return cacheManager -> cacheManager.setAllowNullValues(false);
    }
}

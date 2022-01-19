package org.unidue.ub.libintel.almaconnector.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.unidue.ub.libintel.almaconnector.listener.RedisListener;
import org.unidue.ub.libintel.almaconnector.model.run.SapDataRun;

@Configuration
public class RedisConfiguration {

    /**
     * using Jackson JSON serializer to avoid stack overflow due to invoice object with back references
     * @param redisConnectionFactory the redis connection factory bean
     * @return the redis template to save SapDataRun objects
     */
    @Bean
    RedisTemplate<String, SapDataRun> redisSapDataRunTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, SapDataRun> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(SapDataRun.class));
        return template;
    }

    /**
     * registers a redis message listener which listens to the expiration of cached hooks
     * @param connectionFactory the redis connection factory bean
     * @param redisListener the redis listener bean
     * @return a redis listener container
     */
    @Bean
    public RedisMessageListenerContainer getListenerContainer(RedisConnectionFactory connectionFactory, RedisListener redisListener) {
        //Create connection container
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        //Put in redis connection
        container.setConnectionFactory(connectionFactory);
        //Write the type to be monitored, i.e. timeout monitoring
        Topic topic = new PatternTopic("__keyevent@0__:expired");
        container.addMessageListener(messageListener(redisListener), topic);
        return container;
    }

    /**
     * the listener adapter foor the redis listener container
     * @param redisListener the redis listener bean
     * @return the message listener adapter
     */
    @Bean
    MessageListenerAdapter messageListener(RedisListener redisListener) {
        return new MessageListenerAdapter(redisListener);
    }
}

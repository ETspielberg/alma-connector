package org.unidue.ub.libintel.almaconnector.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.unidue.ub.libintel.almaconnector.listener.RedisListener;

@Configuration
public class RedisListenerConfiguration {

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

    @Bean
    MessageListenerAdapter messageListener(RedisListener redisListener) {
        return new MessageListenerAdapter(redisListener);
    }
}

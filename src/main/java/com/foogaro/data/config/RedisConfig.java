package com.foogaro.data.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Value("${spring.cache.redis.time-to-live}")
    private String timeToLive;

    @Value("${spring.cache.redis.key-prefix}")
    private String keyPrefix;

    @Value("${spring.cache.redis.use-key-prefix}")
    private boolean useKeyPrefix;

    @Value("${spring.cache.redis.cache-null-values}")
    private boolean cacheNullValues;

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig(Thread.currentThread().getContextClassLoader())
            .entryTtl(Duration.parse("PT" + timeToLive))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        if (useKeyPrefix) {
            config = config.prefixCacheNameWith(keyPrefix);
        }

        if (!cacheNullValues) {
            config = config.disableCachingNullValues();
        }

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .build();
    }
}
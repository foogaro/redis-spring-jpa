package com.foogaro.data.config;

import com.foogaro.data.config.properties.StandaloneConfigurationProperties;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import redis.clients.jedis.JedisPoolConfig;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;
import java.time.Duration;
import java.util.Optional;

@Configuration
@ConditionalOnProperty(
        value = "spring.redis.mode",
        havingValue = "standalone",
        matchIfMissing = true
)
public class RedisStandaloneConfig {

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    @Bean
    public RedisStandaloneConfiguration redisStandaloneConfiguration(StandaloneConfigurationProperties standaloneConfigurationProperties) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(standaloneConfigurationProperties.getHost());
        redisStandaloneConfiguration.setPort(standaloneConfigurationProperties.getPort());
        redisStandaloneConfiguration.setUsername(standaloneConfigurationProperties.getUsername());
        redisStandaloneConfiguration.setPassword(standaloneConfigurationProperties.getPassword());
        return redisStandaloneConfiguration;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(RedisStandaloneConfiguration redisStandaloneConfiguration, JedisClientConfiguration jedisClientConfiguration) {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration);
        return jedisConnectionFactory;
    }

    @Bean
    public JedisClientConfiguration jedisClientConfiguration(JedisPoolConfig jedisPoolConfig, StandaloneConfigurationProperties standaloneConfigurationProperties) {
        return new JedisClientConfiguration() {
            @Override
            public boolean isUseSsl() {
                return false;
            }

            @Override
            public Optional<SSLSocketFactory> getSslSocketFactory() {
                return Optional.empty();
            }

            @Override
            public Optional<SSLParameters> getSslParameters() {
                return Optional.empty();
            }

            @Override
            public Optional<HostnameVerifier> getHostnameVerifier() {
                return Optional.empty();
            }

            @Override
            public boolean isUsePooling() {
                return getPoolConfig().isPresent();
            }

            @Override
            public Optional<GenericObjectPoolConfig> getPoolConfig() {
                return Optional.of(jedisPoolConfig);
            }

            @Override
            public Optional<String> getClientName() {
                return Optional.ofNullable(standaloneConfigurationProperties.getClientName());
            }

            @Override
            public Duration getConnectTimeout() {
                return Duration.ofMillis(standaloneConfigurationProperties.getConnectTimeout());
            }

            @Override
            public Duration getReadTimeout() {
                return Duration.ofMillis(standaloneConfigurationProperties.getTimeout());
            }
        };
    }

    @Bean
    public JedisPoolConfig jedisPoolConfig(@Value("${spring.redis.jedis.pool.max-active:500}") int maxTotal, @Value("${spring.redis.jedis.pool.max-idle:400}") int maxIdle,
                                           @Value("${spring.redis.jedis.pool.min-idle:1}") int minIdle, @Value("${spring.redis.jedis.pool.max-wait:100}") int maxWait,
                                           @Value("${spring.redis.jedis.pool.time-between-eviction-runs}") int timeBetweenEvictionRuns) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setMaxWait(Duration.ofMillis(maxWait));
        config.setTimeBetweenEvictionRuns(Duration.ofMillis(timeBetweenEvictionRuns));
        return config;
    }

}

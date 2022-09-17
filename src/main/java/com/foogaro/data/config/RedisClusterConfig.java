package com.foogaro.data.config;

import com.foogaro.data.config.properties.ClusterConfigurationProperties;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
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
        havingValue = "cluster",
        matchIfMissing = false
)
public class RedisClusterConfig {

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    @Bean
    public RedisClusterConfiguration redisClusterConfiguration(ClusterConfigurationProperties clusterConfigurationProperties) {
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(clusterConfigurationProperties.getNodes());
        redisClusterConfiguration.setMaxRedirects(clusterConfigurationProperties.getMaxRedirects());
        redisClusterConfiguration.setUsername(clusterConfigurationProperties.getUsername());
        redisClusterConfiguration.setPassword(RedisPassword.of(clusterConfigurationProperties.getPassword()));
        return redisClusterConfiguration;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(RedisClusterConfiguration redisClusterConfiguration, JedisClientConfiguration jedisClientConfiguration) {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisClusterConfiguration, jedisClientConfiguration);
        jedisConnectionFactory.afterPropertiesSet();
        return jedisConnectionFactory;
    }

    @Bean
    public JedisClientConfiguration jedisClientConfiguration(JedisPoolConfig jedisPoolConfig, ClusterConfigurationProperties clusterConfigurationProperties) {

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
                return Optional.ofNullable(clusterConfigurationProperties.getClientName());
            }

            @Override
            public Duration getConnectTimeout() {
                return Duration.ofMillis(clusterConfigurationProperties.getConnectTimeout());
            }

            @Override
            public Duration getReadTimeout() {
                return Duration.ofMillis(clusterConfigurationProperties.getTimeout());
            }
        };
    }

    @Bean
    public JedisPoolConfig jedisPoolConfig(@Value("${jedis.pool.max.total:500}") int maxTotal, @Value("${jedis.pool.max.idle:400}") int maxIdle,
                                           @Value("${jedis.pool.min.idle:1}") int minIdle, @Value("${jedis.pool.max.wait:100}") int maxWait) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setMaxWaitMillis(Duration.ofMillis(maxWait).toMillis());
        return config;
    }

}

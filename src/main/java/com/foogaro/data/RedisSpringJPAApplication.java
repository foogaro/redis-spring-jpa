package com.foogaro.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@EnableCaching
@EnableRedisRepositories
@SpringBootApplication
public class RedisSpringJPAApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedisSpringJPAApplication.class, args);
	}

}

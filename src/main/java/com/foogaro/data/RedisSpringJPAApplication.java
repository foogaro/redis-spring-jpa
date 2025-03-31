package com.foogaro.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@EnableCaching
@EnableRedisRepositories
@SpringBootApplication
public class SpringRedisL2CacheApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringRedisL2CacheApplication.class, args);
	}

}

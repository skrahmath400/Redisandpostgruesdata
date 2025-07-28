package com.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RedisImplApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedisImplApplication.class, args);
	}

}

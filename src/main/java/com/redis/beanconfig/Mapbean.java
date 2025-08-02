package com.redis.beanconfig;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Mapbean {
	@Bean
	public ModelMapper getmapper() {
		return new ModelMapper();
	}
}

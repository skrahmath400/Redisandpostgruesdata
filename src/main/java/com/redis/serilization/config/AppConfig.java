package com.redis.serilization.config;

 import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// Or your main config package

	import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

	@Configuration
	public class AppConfig {

	    /**
	     * ✅ Defines a properly configured ObjectMapper bean that handles Java 8 time types.
	     */
//	    @Bean
//	    public ObjectMapper objectMapper() {
//	        ObjectMapper objectMapper = new ObjectMapper();
//	        // Add the module that handles Instant, LocalDateTime, etc.
//	        objectMapper.registerModule(new JavaTimeModule());
//	        // Enable default typing to store class info in the JSON for complex objects.
//	        objectMapper.activateDefaultTyping(
//	            LaissezFaireSubTypeValidator.instance,
//	            ObjectMapper.DefaultTyping.NON_FINAL,
//	            JsonTypeInfo.As.PROPERTY
//	        );
//	        return objectMapper;
//	    }

	    /**
	     * ✅ Defines the cache configuration using our custom ObjectMapper.
	     * This will be used by the CacheManager.
	     */
	    @Bean
	    public RedisCacheConfiguration cacheConfiguration() {
	        return RedisCacheConfiguration.defaultCacheConfig()
	            // This now acts as a pure 2-minute idle timeout
	            .entryTtl(Duration.ofMinutes(2))
	            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
	            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
	    }
	    /**
	     * ✅ Defines the CacheManager using the configuration above.
	     */

	    @Bean
	    public CacheManager cacheManager(RedisConnectionFactory connectionFactory, RedisCacheConfiguration cacheConfiguration) {
	        return RedisCacheManager.builder(connectionFactory)
	            .cacheDefaults(cacheConfiguration)
	            .build();
	    }
	    
	    
	    /**
	     * ✅ (Optional) Defines a RedisTemplate that also uses the correct ObjectMapper.
	     */
//	    @Bean
//	    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
//	        RedisTemplate<String, Object> template = new RedisTemplate<>();
//	        template.setConnectionFactory(connectionFactory);
//
//	        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
//	        StringRedisSerializer stringSerializer = new StringRedisSerializer();
//	        
//	        template.setKeySerializer(stringSerializer);
//	        template.setValueSerializer(serializer);
//	        template.setHashKeySerializer(stringSerializer);
//	        template.setHashValueSerializer(serializer);
//
//	        template.afterPropertiesSet();
//	        return template;
//	    }
	}
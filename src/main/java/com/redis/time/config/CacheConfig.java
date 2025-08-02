//package com.redis.time.config;
//
//// In your CacheConfig.java
//
//import java.time.Duration;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.cache.RedisCacheConfiguration;
//import org.springframework.data.redis.cache.RedisCacheManager;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.RedisSerializationContext;
//
//@Configuration
//public class CacheConfig {
//
//    @Bean
//    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
//        
//        // This TTL will now act as our 1-MINUTE IDLE TIMEOUT.
//        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())); // Recommended for custom objects
//
//
//        return RedisCacheManager.builder(connectionFactory)
//                .cacheDefaults(defaultConfig)
//                .build();
//    }
//}
package com.redis.time.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

//
//import java.util.List;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//
//// Adjust package as needed
//
//import com.redis.dto.RoleScreen;
//
//@Configuration
//public class RedisConfig {
//
//	@Bean
//	public RedisTemplate<String, List<RoleScreen>> redisTemplate(RedisConnectionFactory connectionFactory) {
//		RedisTemplate<String, List<RoleScreen>> template = new RedisTemplate<>();
//		template.setConnectionFactory(connectionFactory);
//		// Optional: Add serializers if needed (e.g., for custom JSON handling)
//		// template.setValueSerializer(new Jackson2JsonRedisSerializer<>(List.class));
//		return template;
//	}
//}

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // ✅ Use String serializer for keys to avoid extra quotes
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        // ✅ Use JSON serializer for values
        Jackson2JsonRedisSerializer<Object> valueSerializer = new Jackson2JsonRedisSerializer<>(Object.class);

        // Apply serializers
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);

        template.afterPropertiesSet();
        return template;
    }
}

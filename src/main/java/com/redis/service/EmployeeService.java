package com.redis.service;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.redis.dto.RoleScreen;
import com.redis.entity.Employee;
import com.redis.entity.Overalldata;
import com.redis.entity.UserRole;
import com.redis.handler.DataNotFoundException;
import com.redis.repository.EmployeeRepository;
import com.redis.repository.Rolerepo;
import com.redis.wrapper.CacheWrapper;
import jakarta.persistence.EntityNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException; // ✅ CORRECT IMPORT
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final Rolerepo rpoobj;
    private final ModelMapper mobj;
    
    // This RedisTemplate is now created and configured manually inside this class.
    private final RedisTemplate<String, Object> localRedisTemplate;

    private final Duration ABSOLUTE_TTL = Duration.ofMinutes(2);
    private final Duration IDLE_TTL = Duration.ofMinutes(1);

    public EmployeeService(
        EmployeeRepository employeeRepository,
        Rolerepo rpoobj,
        ModelMapper mobj,
        RedisConnectionFactory connectionFactory // Only inject the factory
    ) {
        this.employeeRepository = employeeRepository;
        this.rpoobj = rpoobj;
        this.mobj = mobj;
        
        // Manually create and configure the RedisTemplate to bypass any conflicts.
        this.localRedisTemplate = this.createConfiguredRedisTemplate(connectionFactory);
    }

    private RedisTemplate<String, Object> createConfiguredRedisTemplate(RedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        );
        
        Jackson2JsonRedisSerializer<Object> jacksonSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
        
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jacksonSerializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jacksonSerializer);
        template.afterPropertiesSet();
        
        return template;
    }

//    @Cacheable(value = "employeeCache", key = "#employeeId")
//    public Employee getEmployeeWithRoles(Long employeeId) {
//        System.out.println("⚠️ Fetching employee " + employeeId + " from DB.");
//        return employeeRepository
//            .findByIdWithRoles(employeeId)
//            .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
//    }

    public List<Overalldata> getsomedata(int empid, String pass) {
        String cacheKey = "tabledata::" + empid + "-" + pass;
        Object cachedValue = null;

        try {
            cachedValue = this.localRedisTemplate.opsForValue().get(cacheKey);
        } catch (SerializationException e) {
            System.err.println("⚠️ Corrupt data found in cache for key: " + cacheKey + ". Deleting it now.");
            this.localRedisTemplate.delete(cacheKey);
        }

        if (cachedValue != null) {
            CacheWrapper<List<Overalldata>> wrapper = (CacheWrapper<List<Overalldata>>) cachedValue;
            long lifetimeInMinutes = Duration.between(wrapper.creationTime(), Instant.now()).toMinutes();

            if (lifetimeInMinutes >= ABSOLUTE_TTL.toMinutes()) {
                System.out.println("⚠️ Cache HIT for key " + cacheKey + ", but expired by 2-min absolute TTL. Refetching...");
                this.localRedisTemplate.delete(cacheKey);
                return fetchSomeDataFromDBAndCache(empid, pass, cacheKey);
            }

            System.out.println("✅ Cache HIT for key " + cacheKey + ". Resetting 1-min idle timer.");
            this.localRedisTemplate.opsForValue().set(cacheKey, wrapper, IDLE_TTL);
            return wrapper.data();
        }

        System.out.println("⚠️ Cache MISS for key " + cacheKey + ". Fetching from DB.");
        return fetchSomeDataFromDBAndCache(empid, pass, cacheKey);
    }

    private List<Overalldata> fetchSomeDataFromDBAndCache(int empid, String pass, String cacheKey) {
        List<Overalldata> result = employeeRepository.findUserByEmployOveralldata(empid, pass);
        if (result.isEmpty()) {
            throw new DataNotFoundException("ID not found for empid: " + empid);
        }
        var newWrapper = new CacheWrapper<>(result, Instant.now());
        this.localRedisTemplate.opsForValue().set(cacheKey, newWrapper, IDLE_TTL);
        return result;
    }

    @Cacheable(value = "allroles")
    public List<UserRole> getallroles() {
        System.out.println("⚠️ Fetching all roles from DB.");
        return rpoobj.findAll();
    }

    public List<RoleScreen> getroleandscreen(String role_name) {
        String sanitizedRoleName = role_name.replace("\"", "").trim();
        String dataKey = "roleandscreen::" + sanitizedRoleName;

        Object cachedResult = this.localRedisTemplate.opsForValue().get(dataKey);
        if (cachedResult != null) {
            return (List<RoleScreen>) cachedResult;
        }

        List<RoleScreen> dbResult = employeeRepository.findUserByEmployOveralldatas(sanitizedRoleName);
        if (dbResult.isEmpty()) {
            throw new DataNotFoundException("No role found for role_name: " + sanitizedRoleName);
        }

        List<RoleScreen> mappedResult = dbResult
            .stream()
            .map(e -> mobj.map(e, RoleScreen.class))
            .collect(Collectors.toList());

        String actualRoleName = mappedResult.get(0).getRolename();
        if ("ZONAL".equals(actualRoleName) || "TL".equals(actualRoleName) || "FULLSTACK".equals(actualRoleName)) {
            this.localRedisTemplate.opsForValue().set(dataKey, mappedResult, 24, TimeUnit.HOURS);
        } else {
            this.localRedisTemplate.opsForValue().set(dataKey, mappedResult, 2, TimeUnit.MINUTES);
        }

        return mappedResult;
    }
}
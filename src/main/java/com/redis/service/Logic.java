package com.redis.service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.dto.JwtResponse;
import com.redis.entity.Overalldata;
import com.redis.handler.DataNotFoundException;
import com.redis.jwttoken.JwtService;
import com.redis.repository.Overalltablerepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class Logic {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final Overalltablerepo employeeRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final ObjectMapper objectMapper;

    // Method to fetch data from DB and cache it
    public List<Overalldata> fetchSomeDataFromDBAndCache(int empid, String pass) {
        List<Overalldata> result = employeeRepository.findUserByEmployOveralldata(empid, pass);
        if (result.isEmpty()) {
            throw new DataNotFoundException("ID not found for empid: " + empid);
        }
        return result;
    }

    /**
     * Handles user login, issues a JWT, and caches role permissions.
     * If permissions for a role are already in Redis, it fetches them from the cache.
     * Otherwise, it gets them from the database and stores them in Redis for subsequent users with the same role.
     */
    public JwtResponse loginAndIssueToken(int empid, String rawPassword) {
        // 1. Retrieve employee data by empid from the database
        Overalldata emp = employeeRepository.findByEmployeeId(empid)
                .orElseThrow(() -> new DataNotFoundException("Employee not found: " + empid));

        // 2. Verify the password (Handles both BCrypt and plain-text for legacy support)
        String storedPassword = emp.getPassword();
        boolean isPasswordMatch;

        if (storedPassword == null) {
            isPasswordMatch = false;
        } else if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            // The stored password is a BCrypt hash
            isPasswordMatch = encoder.matches(rawPassword, storedPassword);
        } else {
            // The stored password is plain text
            isPasswordMatch = storedPassword.equals(rawPassword);
        }

        if (!isPasswordMatch) {
            throw new DataNotFoundException("Invalid username/password");
        }


        // 3. Define the Redis key based on the role name (e.g., "role_permissions:TL")
        String roleName = emp.getRoleName();
        if (roleName == null || roleName.isBlank()) {
            throw new DataNotFoundException("User does not have a role assigned.");
        }
        String roleRedisKey = "role_permissions:" + roleName;

        // 4. Try to fetch the permissions payload from Redis
        Map<String, Object> permissionsPayload = null;
        try {
            permissionsPayload = (Map<String, Object>) redisTemplate.opsForValue().get(roleRedisKey);
        } catch (Exception e) {
            log.warn("Could not retrieve data from Redis for key '{}'. Falling back to database. Error: {}", roleRedisKey, e.getMessage());
        }


        // 5. Check if data was found in Redis (Cache Miss vs. Cache Hit)
        if (permissionsPayload == null) {
            log.info("CACHE MISS: No permissions found in Redis for role '{}'.", roleName);
            // CACHE MISS: Data not in Redis, so build it from the database
            permissionsPayload = new HashMap<>();

            // Store role_name -> "role"
            permissionsPayload.put("role", roleName);

            // Convert screen_permissions to Map<String, Object>
            if (emp.getScreen_permissions() != null && !emp.getScreen_permissions().isNull()) {
                Map<String, Object> permsMap = objectMapper.convertValue(
                        emp.getScreen_permissions(),
                        new TypeReference<Map<String, Object>>() {}
                );
                permissionsPayload.put("perms", permsMap);
            }

            // Store the newly created payload in Redis for 60 minutes
            redisTemplate.opsForValue().set(roleRedisKey, permissionsPayload, Duration.ofMinutes(60));
            log.info("CACHE POPULATED: Stored permissions for role '{}' in Redis.", roleName);

        } else {
            log.info("CACHE HIT: Found permissions in Redis for role '{}'. Using cached data.", roleName);
            // CACHE HIT: Data was found in Redis, no need to do anything else.
        }

        // 6. Create the final JWT claims. Start with the cached/newly created permissions.
        // It's good practice to create a new map to avoid modifying the cached object directly.
        Map<String, Object> finalClaims = new HashMap<>(permissionsPayload);

        // Add a unique session ID (sid) for this specific login session. This should NOT be cached.
        finalClaims.put("sid", java.util.UUID.randomUUID().toString());

        // 7. Generate the JWT Token with the final claims
        String token = jwtService.generateToken(emp, finalClaims);
        long exp = jwtService.getExpiryEpochSeconds(token);

        return new JwtResponse(token, exp, "Bearer");
    }
}

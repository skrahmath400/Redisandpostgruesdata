package com.redis.schedulingconfig;


import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RedisAutoCleanup {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Scheduled(fixedRate = 60000) // every 60 seconds
    public void cleanupExpiredRoleScreenData() {
        Set<String> dataKeys = redisTemplate.keys("roleandscreen::*");

        if (dataKeys == null || dataKeys.isEmpty()) return;

        for (String dataKey : dataKeys) {
            String roleName = dataKey.replace("roleandscreen::", "");
            String accessKey = "access::roleandscreen::" + roleName;

            Boolean accessExists = redisTemplate.hasKey(accessKey);

            if (accessExists == null || !accessExists) {
                redisTemplate.delete(dataKey);
                System.out.println("🧹 Deleted stale role data for: " + dataKey + " (no access key)");
            }
        }
    }
}

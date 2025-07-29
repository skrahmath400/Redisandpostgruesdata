package com.redis.dto;

import com.fasterxml.jackson.databind.JsonNode;
//import io.hypersistence.utils.hibernate.type.json.JsonNodeBinaryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleScreen implements Serializable {
    private static final long serialVersionUID = 1L;  // Add this for versioning compatibility
    
    private String rolename;
    private JsonNode screen_permissions;  // No need for @Column annotation here
}

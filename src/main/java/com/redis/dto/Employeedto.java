package com.redis.dto;


import org.hibernate.annotations.Type;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;

import jakarta.persistence.Column;

public class Employeedto {
	int employeeId;
	String password;
	String role_name;
    // Use JsonNode for parsing JSON data in a structured way
    @Type(JsonNodeBinaryType.class)
    @Column(name="screen_permissions", columnDefinition = "jsonb")
    private JsonNode screen_permissions;  // This stores the JSON object directly, no need to handle it as a string
}

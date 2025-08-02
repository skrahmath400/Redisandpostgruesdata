package com.redis.entity;

import java.io.Serializable;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.Immutable;

import com.fasterxml.jackson.databind.JsonNode;

import io.hypersistence.utils.hibernate.type.json.JsonNodeBinaryType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Immutable
@Table(name = "emp_role_scr_per", schema = "sce_admin")
public class Overalldata implements Serializable {
    
    private static final long serialVersionUID = 1L; 

    @Id
    @Column(name = "employee_id")
    private int employeeId;

    @Column(name="password")
    private String password;

    @Column(name = "first_name") // Explicit mapping if column name differs; adjust if needed
    private String first_name;

    @Column(name = "last_name")
    private String last_name;

    @Column(name = "role_name")
    private String  roleName;
//    

    // Use JsonNode for parsing JSON data in a structured way
    @Type(JsonNodeBinaryType.class)
    @Column(name="screen_permissions", columnDefinition = "jsonb")
    private JsonNode screen_permissions;  // This stores the JSON object directly, no need to handle it as a string
   
}
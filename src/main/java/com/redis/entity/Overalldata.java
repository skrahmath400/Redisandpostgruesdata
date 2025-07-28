package com.redis.entity;

import java.io.Serializable;
//import org.hibernate.annotations.Type;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.annotation.TypeAlias;

import com.fasterxml.jackson.databind.JsonNode;

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

    private String first_name;
    private String last_name;
    private String role_name;
    

    // Use JsonNode for parsing JSON data in a structured way
    @Column(name="screen_permission", columnDefinition = "json")
    private JsonNode screen_permission;  // This stores the JSON object directly, no need to handle it as a string
    
}

package com.redis.dto;

import java.util.List;

import com.redis.entity.Employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class EmployeeWithRolesDTO {
    private Employee employee;
    private List<String> roles;
}

package com.redis.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.redis.dto.RoleScreen;
import com.redis.entity.Employee;
import com.redis.entity.Overalldata;
import com.redis.entity.Roledata;
import com.redis.entity.UserRole;
import com.redis.handler.DataNotFoundException;
import com.redis.service.EmployeeService;

@CrossOrigin
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

//	@GetMapping("/{id}")
//	public ResponseEntity<Employee> getEmployee(@PathVariable Long id) {
//		return ResponseEntity.ok(employeeService.getEmployeeWithRoles(id));
//	}

	@GetMapping("/getroles")
	public List<UserRole> getallroles() {
		return employeeService.getallroles();
	}

//	@GetMapping("/datacall")
//	public String getdata() {
//		return employeeService.addingdata();
//	}
//
//	@GetMapping("/getrolesdata")
//	public Roledata getroledata(@RequestParam String name) {
//		return employeeService.findbyrolename(name);
//
//	}
//
//	@GetMapping("/getallrolescheckanddataacess/{rolename}/{id}")
//	public Optional<Employee> getallroleanddata(@PathVariable String rolename, @PathVariable Long id) {
//		return employeeService.getallroleanddata(rolename, id);
//
//	}
//
//	@GetMapping("/alldata")
//	public Overalldata getwholedata(@RequestParam int empid, @RequestParam String password) {
//		return employeeService.getwholedata(empid, password);
//
//		
//	}

//	@GetMapping("/getEmployeedata")
//	public Overallexample getEmployeedata(@RequestParam int empid, @RequestParam String password) {
//		return employeeService.getEmployeeData(empid, password);
//	}

	@GetMapping("/overalldatabyview")
	public List<Overalldata> getoveralldata(@RequestParam int id, @RequestParam String passw) {
		return employeeService.getsomedata(id, passw);

	}

	@GetMapping("/overallexampledatabytable")
	public ResponseEntity<Object> getsomedata(@RequestParam int id, @RequestParam String passw) {
		try {
			List<Overalldata> data = employeeService.getsomedata(id, passw);
			return ResponseEntity.ok(data);
		} catch (DataNotFoundException ex) {
			Map<String, String> error = new HashMap<>();
			error.put("message", ex.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		}
	}

//	@GetMapping("/role/screenpermission/")
//	public List<Overalldata> getroleandscreenpermssion()
	@GetMapping("/getroleandscreen")
	public ResponseEntity<Object> getroleandscreen(@RequestParam String rolename) {
		try {
			List<RoleScreen> data = employeeService.getroleandscreen(rolename);
			return ResponseEntity.ok(data);
		} catch (DataNotFoundException ex) {
			Map<String, String> error = new HashMap<>();
			error.put("message", ex.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		}
	}

}

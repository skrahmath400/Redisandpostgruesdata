package com.redis.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.redis.entity.Employee;
import com.redis.entity.Overalldata;
import com.redis.entity.Overallexample;
import com.redis.entity.Roledata;
import com.redis.entity.UserRole;
import com.redis.handler.DataNotFoundException;
import com.redis.repository.EmployeeRepository;
import com.redis.repository.Overallexamplerepo;
import com.redis.repository.Overalltablerepo;
import com.redis.repository.Roledatarepo;
import com.redis.repository.Rolerepo;

import jakarta.persistence.EntityNotFoundException;

@Service
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private Rolerepo rpoobj;
	@Autowired
	Roledatarepo roledatarepo;
	@Autowired
	Overalltablerepo alldata;

	@Autowired
	Overallexamplerepo overallrepo;

	@Cacheable(value = "employeeCache", key = "#a0")
	public Employee getEmployeeWithRoles(Long employeeId) {
		return employeeRepository.findByIdWithRoles(employeeId)
				.orElseThrow(() -> new EntityNotFoundException("Employee not found"));
	}

	@Cacheable(value = "allroles")
	public List<UserRole> getallroles() {

		return rpoobj.findAll();
	}

	@Cacheable(value = "staticdata")
	public String addingdata() {
		return "helloworld";
	}

	@Cacheable(value = "role_table", key = "#roleName")
	public Roledata findbyrolename(String roleName) {

		return roledatarepo.findByRoleName(roleName);
	}

	@Cacheable(value = "roleandemp")
	public Optional<Employee> getallroleanddata(String roleName, Long id) {

		Roledata result = roledatarepo.findByRoleName(roleName);
		String rolename = result.getRoleName();
		if (roleName.equals(rolename)) {
			return employeeRepository.findById(id);
		}
		return null;
	}

//
//	public Overallexample getwholedata(int empid, String password) {
//		return employeeRepository.findUserByEmployOverallexample(empid, password);
//	}
	@Cacheable(value = "viewdata")
	public List<Overallexample> getEmployeeData(int empid, String password) {
		return overallrepo.findByEmployeeIdAndPassword(empid, password);
	}
//
	@Cacheable(value = "tabledata" ,key = "#empid + '-' + #pass")
	public List<Overalldata> getsomedata(int empid, String pass) {
		List<Overalldata>  result=	  employeeRepository.findUserByEmployOveralldata(empid, pass);
		if(result.isEmpty()) {
			throw new DataNotFoundException("ID not found for empid: " + empid);
		}
	   return result;
	}
	
}

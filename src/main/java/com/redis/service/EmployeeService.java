package com.redis.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.redis.dto.RoleScreen;
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
	private final EmployeeRepository employeeRepository;
	private final Rolerepo rpoobj;
	private final Roledatarepo roledatarepo;
	private final Overalltablerepo alldata;
	private final Overallexamplerepo overallrepo;
	private final ModelMapper mobj;
	private final RedisTemplate<String, Object> redisTemplate; // Changed to <String, Object> for flexibility

	// Constructor injection for all dependencies
	public EmployeeService(EmployeeRepository employeeRepository, Rolerepo rpoobj, Roledatarepo roledatarepo,
			Overalltablerepo alldata, Overallexamplerepo overallrepo, ModelMapper mobj,
			RedisTemplate<String, Object> redisTemplate) {
		this.employeeRepository = employeeRepository;
		this.rpoobj = rpoobj;
		this.roledatarepo = roledatarepo;
		this.alldata = alldata;
		this.overallrepo = overallrepo;
		this.mobj = mobj;
		this.redisTemplate = redisTemplate;
	}

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

	@Cacheable(value = "viewdata")
	public List<Overallexample> getEmployeeData(int empid, String password) {
		return overallrepo.findByEmployeeIdAndPassword(empid, password);
	}

	@Cacheable(value = "tabledata", key = "#empid + '-' + #pass")
	public List<Overalldata> getsomedata(int empid, String pass) {
		List<Overalldata> result = employeeRepository.findUserByEmployOveralldata(empid, pass);
		if (result.isEmpty()) {
			throw new DataNotFoundException("ID not found for empid: " + empid);
		}
		return result;
	}

	@Cacheable(value = "roleandscreen", key = "#role_name")
	public List<RoleScreen> getroleandscreen(String role_name) {

		// Sanitize the role_name to remove extra quotes or whitespace
		role_name = role_name.replace("\"", "").trim();

		// Fetch data from DB
		List<RoleScreen> result = employeeRepository.findUserByEmployOveralldatas(role_name);

		if (result.isEmpty()) {
			throw new DataNotFoundException("No role found for role_name: " + role_name);
		}

		// Extract role name from first result (for TTL decision)
		String roleName = result.get(0).getRolename();

		// Map entity list to DTO list
		List<RoleScreen> mappedResult = result.stream().map(e -> mobj.map(e, RoleScreen.class))
				.collect(Collectors.toList());

		// Define Redis key and TTL based on role
		String dataKey = "roleandscreen::" + role_name;
		Duration dataTtl = (roleName.equals("ZONAL") || roleName.equals("TL") || roleName.equals("FULLSTACK"))
				? Duration.ofHours(24)
				: Duration.ofMinutes(1);

		// Log the keys for debugging
		System.out.println("Storing data key in Redis: " + dataKey);

		// Manually store with custom TTL (overwrite @Cacheable if needed)
		redisTemplate.opsForValue().set(dataKey, mappedResult, dataTtl);

		// Set last access timestamp with 2-minute TTL
		String accessKey = "access::roleandscreen::" + role_name;
		long accessTimestamp = Instant.now().toEpochMilli();
		System.out.println("Storing access timestamp in Redis: " + accessKey);
		redisTemplate.opsForValue().set(accessKey, accessTimestamp, Duration.ofMinutes(2));

		return mappedResult;
	}

	public Long getLastAccessTime(String role_name) {
	    role_name = role_name.replace("\"", "").trim(); // sanitize input
	    String accessKey = "access::roleandscreen::" + role_name;
	    Object timestamp = redisTemplate.opsForValue().get(accessKey);
	    return (timestamp != null) ? (Long) timestamp : null;
	}
}
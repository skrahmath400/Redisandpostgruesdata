	package com.redis.repository;
	
	import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.redis.dto.RoleScreen;
//	import com.redis.dto.RoleScreen;
	import com.redis.entity.Employee;
import com.redis.entity.Overalldata;
	
	@Repository
	public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	    
	    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.roles WHERE e.employeeId = :id")
	    Optional<Employee> findByIdWithRoles(@Param("id") Long id);
	    @Query("SELECT o FROM Overalldata o WHERE o.employeeId = :empId AND o.password = :password")
	    List<Overalldata> findUserByEmployOveralldata(@Param("empId") int empId, @Param("password") String password);
	    Optional<Overalldata> findByEmployeeId(int employeeId);
	
	//  @Query("Select * FROM Overallexample ov join Employee e ov.employee_id=e.employee_id  where e.employeeId=:empId AND e.password = :password") 
	//  Overallexample findUserByEmployOverallexample(@Param("empId") int empId, @Param("password") String password);
	// 
	
	   
	//   
	//    select * from sce_admin.sce_user_admin sua
	//    join sce_employee se on sua.employee_id =se.employee_id
	//    where se.employee_id =4332 and se."password" ='fcd9e5478572e13901e5c1a562811914';
	 
	    @Query("SELECT new com.redis.dto.RoleScreen(o.roleName, o.screen_permissions) FROM Overalldata o WHERE o.roleName = :roleName")
	    List<RoleScreen> findUserByEmployOveralldatas(@Param("roleName") String roleName);


	
	//    @Query("SELECT new com.redis.dto.RoleScreenDTO(o.roleName, o.screenPermissions) FROM Overalldata o WHERE o.roleName = :roleName")
	//    List<RoleScreen> findUserByEmployOveralldatas(@Param("roleName") String roleName);
	
	
	}

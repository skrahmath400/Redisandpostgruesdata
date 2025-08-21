package com.redis.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.redis.entity.Overalldata;

@Repository
public interface Overalltablerepo extends JpaRepository<Overalldata, Integer>{
	@Query("SELECT o FROM Overalldata o WHERE o.employeeId = :empId AND o.password = :password")
	List<Overalldata> findUserByEmployOveralldata(@Param("empId") int empId, @Param("password") String password);
    Optional<Overalldata> findByEmployeeId(int employeeId);

}

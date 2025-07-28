package com.redis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.redis.entity.Overallexample;

@Repository
public interface Overallexamplerepo extends JpaRepository<Overallexample, Integer> {

	List<Overallexample> findByEmployeeIdAndPassword(int employeeId, String password);
}

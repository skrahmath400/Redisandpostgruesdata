package com.redis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.redis.entity.Overalldata;

@Repository
public interface Overalltablerepo extends JpaRepository<Overalldata, Integer>{
	
	

}

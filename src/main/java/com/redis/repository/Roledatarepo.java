package com.redis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.redis.entity.Roledata;

@Repository

public interface Roledatarepo extends JpaRepository<Roledata, Integer> {
	public Roledata findByRoleName(String roleName);

}

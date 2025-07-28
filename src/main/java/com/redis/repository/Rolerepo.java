package com.redis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.redis.entity.UserRole;

@Repository
public interface Rolerepo extends JpaRepository<UserRole, Long> {

}

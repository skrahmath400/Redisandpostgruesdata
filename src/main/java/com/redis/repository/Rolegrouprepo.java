package com.redis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.redis.entity.Rolegroup;

@Repository
public interface Rolegrouprepo extends JpaRepository<Rolegroup, Integer> {

}

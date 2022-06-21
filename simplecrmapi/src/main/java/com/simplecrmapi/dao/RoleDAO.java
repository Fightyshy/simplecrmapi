package com.simplecrmapi.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.simplecrmapi.entity.Role;

@Repository
public interface RoleDAO extends CrudRepository<Role,Integer>{
	Role findRoleByName(String name);
}

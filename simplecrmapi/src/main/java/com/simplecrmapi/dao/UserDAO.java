package com.simplecrmapi.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.simplecrmapi.entity.User;
@Repository
public interface UserDAO extends JpaRepository<User, Integer>{
	User findByUsername(String username);
}

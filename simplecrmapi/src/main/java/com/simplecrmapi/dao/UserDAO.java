package com.simplecrmapi.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.simplecrmapi.entity.User;
@Repository
public interface UserDAO extends JpaRepository<User, Integer>{
	@Query("SELECT u FROM User u left join fetch u.employee WHERE u.employee.id=:id")
	User findByEmployeeID(@Param("id") int EmployeeID);
	
	User findByUsername(String username);
	
	@Query("SELECT u FROM User u left join fetch u.employee WHERE u.employee.emailAddress=:email")
	User findByEmail(@Param("email") String email);
}

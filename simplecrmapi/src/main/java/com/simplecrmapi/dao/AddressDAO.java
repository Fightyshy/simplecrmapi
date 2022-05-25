package com.simplecrmapi.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.simplecrmapi.entity.Address;

public interface AddressDAO extends JpaRepository<Address, Integer>{
	//Using autogen atm, custom methods as needed
}

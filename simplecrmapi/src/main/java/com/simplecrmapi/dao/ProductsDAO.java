package com.simplecrmapi.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.simplecrmapi.entity.Products;

public interface ProductsDAO extends JpaRepository<Products, Integer> {
	Products findByName(String name);
	
	Products deleteByName(String name);
}

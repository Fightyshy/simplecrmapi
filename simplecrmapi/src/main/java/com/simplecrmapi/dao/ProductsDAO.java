package com.simplecrmapi.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.simplecrmapi.entity.Product;

public interface ProductsDAO extends JpaRepository<Product, Integer> {
	Product findByName(String name);
	
	Product deleteByName(String name);
}

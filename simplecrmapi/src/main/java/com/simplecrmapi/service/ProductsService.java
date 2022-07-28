package com.simplecrmapi.service;

import java.util.List;

import com.simplecrmapi.entity.Products;

public interface ProductsService {
	public List<Products> getAllProducts();

	public Products getProductByName(String name);
	
	public Products getProductByID(Integer ID);
	
	public Products saveProduct(Products product);
	
	public Products updateProduct(Products product);
	
	public void deleteProductByName(String name);
	
	public void deleteProductByID(Integer ID);
}

package com.simplecrmapi.service;

import java.util.List;

import com.simplecrmapi.entity.Product;

public interface ProductsService {
	public List<Product> getAllProducts();

	public Product getProductByName(String name);
	
	public Product getProductByID(Integer ID);
	
	public Product saveProduct(Product product);
	
	public Product updateProduct(Product product);
	
	public void deleteProductByName(String name);
	
	public void deleteProductByID(Integer ID);
}

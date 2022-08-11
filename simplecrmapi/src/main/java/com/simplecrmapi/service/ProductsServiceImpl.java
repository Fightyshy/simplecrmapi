package com.simplecrmapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.simplecrmapi.dao.ProductsDAO;
import com.simplecrmapi.entity.Products;

@Service
public class ProductsServiceImpl implements ProductsService {

	@Autowired
	private ProductsDAO productsDAO;
	
	@Override
	public List<Products> getAllProducts() {
		return productsDAO.findAll();
	}

	@Override
	public Products getProductByName(String name) {
		return productsDAO.findByName(name);
	}

	@Override
	public Products getProductByID(Integer ID) {
		Optional<Products> products = productsDAO.findById(ID);
		return products.isEmpty()?null:products.get();
	}

	@Override
	public Products saveProduct(Products product) {
		return productsDAO.save(product);
	}

	@Override
	public Products updateProduct(Products product) {
		return productsDAO.save(product);
	}

	@Override
	public void deleteProductByName(String name) {
		productsDAO.deleteByName(name);
	}

	@Override
	public void deleteProductByID(Integer ID) {
		productsDAO.deleteById(ID);
	}

}

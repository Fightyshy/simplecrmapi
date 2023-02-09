package com.simplecrmapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.simplecrmapi.dao.ProductsDAO;
import com.simplecrmapi.entity.Product;

@Service
public class ProductsServiceImpl implements ProductsService {

	@Autowired
	private ProductsDAO productsDAO;
	
	@Override
	public List<Product> getAllProducts() {
		return productsDAO.findAll();
	}

	@Override
	public Product getProductByName(String name) {
		return productsDAO.findByName(name);
	}

	@Override
	public Product getProductByID(Integer ID) {
		return productsDAO.findById(ID).orElseGet(null);
	}

	@Override
	public Product saveProduct(Product product) {
		product.setId(0);
		return productsDAO.saveAndFlush(product);
	}

	@Override
	public Product updateProduct(Product product) {
		return productsDAO.saveAndFlush(product);
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

package com.simplecrmapi.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.simplecrmapi.entity.Product;
import com.simplecrmapi.service.ProductsService;

@RestController
@RequestMapping("/products")
public class ProductsController {
	@Autowired
	private ProductsService productsService;
	
	@GetMapping("")
	public ResponseEntity<List<Product>> getAllProducts(){
		return ResponseEntity.ok(productsService.getAllProducts());
	}
	
	@GetMapping("/name")
	public ResponseEntity<Product> getproductByName(@RequestParam("product") String name){
		Product product = productsService.getProductByName(name);
		return product==null?ResponseEntity.notFound().build():ResponseEntity.ok(product);
	}
	
	@GetMapping("/id")
	public ResponseEntity<Product> getProductByID(@RequestParam("id") int ID){
		Product product = productsService.getProductByID(ID);
		System.out.println(product);
		return ResponseEntity.ok(product);
	}
	
	//Maybe tags as a separate db table and list?
	
	@PostMapping("")
	public ResponseEntity<Product> saveProduct(@RequestBody Product product) throws URISyntaxException{
		Product newProduct = productsService.saveProduct(product);
		return ResponseEntity.created(new URI("/products/id/"+product.getId())).body(newProduct);
	}
	
	@PutMapping("")
	public ResponseEntity<Product> updateProduct(@RequestBody Product product){
		Product updateProduct = productsService.updateProduct(product);
		return ResponseEntity.ok(updateProduct);
	}
	
	@DeleteMapping("/name")
	public ResponseEntity<Object> deleteProductByName(@RequestParam("product") String name){
		productsService.deleteProductByName(name);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/id")
	public ResponseEntity<Object> deleteProductByID(@RequestParam("id") int ID){
		productsService.deleteProductByID(ID);
		return ResponseEntity.noContent().build();
	}
}

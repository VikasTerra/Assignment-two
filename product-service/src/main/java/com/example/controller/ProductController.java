package com.example.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.entity.EmailDetails;
import com.example.entity.Product;
import com.example.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/product")
public class ProductController {
	@Autowired
	ProductService productService;
	RestTemplate restTemplate;
	
	@PostMapping
	public String createProduct(@Valid @RequestBody Product product) throws Exception {
		productService.saveProduct(product);
		createAndSendProductDetails(product);
		return product.getId();
	}
	@GetMapping("/{id}")
	ResponseEntity<Product> getProduct(@PathVariable String id) {
		Optional<Product> product = productService.getProduct(id);
		
		return generateResponse(product);
	}
	@GetMapping("/name/{name}")
	ResponseEntity<Product> getProductByName(@PathVariable String name) {
		Optional<Product> product = productService.getProductByName(name);
		return generateResponse(product);
	}
	private ResponseEntity<Product> generateResponse(Optional<Product> product) {
		return product.isPresent() ? new ResponseEntity(product, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	private ResponseEntity<String> callmeforCreateProduct(@RequestBody EmailDetails emailDeatails) {
		if (restTemplate == null) {
			restTemplate = new RestTemplate();
		}
		ResponseEntity<String> sys = restTemplate.postForEntity("http://'product-service':8989/api/sendMail",
				emailDeatails, String.class);
		return sys;
	}
	
	private ResponseEntity<String> createAndSendProductDetails(Product product) throws Exception {
		EmailDetails emailDetails = new EmailDetails();
		ObjectMapper mapper = new ObjectMapper();
		emailDetails.setMsgBody(mapper.writeValueAsString(product));
		emailDetails.setRecipient("vikasblr95@gmail.com");
		emailDetails.setSubject("Email Details");
		ResponseEntity<String> response = callmeforCreateProduct(emailDetails);
		return response;
	}

}

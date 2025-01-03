package com.radovan.spring.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.radovan.spring.services.ProductImageService;
import com.radovan.spring.services.ProductService;

@RestController
@RequestMapping(value = "/api/products")
public class GatewayProductController {

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductImageService imageService;

	private void setAuthorizationHeader(String authorizationHeader) {
		RequestContextHolder.getRequestAttributes().setAttribute("Authorization", authorizationHeader,
				RequestAttributes.SCOPE_REQUEST);
	}

	@PostMapping(value = "/createProduct")
	public ResponseEntity<String> createProduct(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@RequestBody JsonNode product) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(productService.createProduct(product), HttpStatus.OK);
	}

	@GetMapping(value = "/productDetails/{productId}")
	public ResponseEntity<JsonNode> getProductDetails(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("productId") Integer productId) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(productService.getBroductById(productId), HttpStatus.OK);
	}

	@PutMapping(value = "/updateProduct/{productId}")
	public ResponseEntity<String> updateProduct(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@RequestBody JsonNode product, @PathVariable("productId") Integer productId) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(productService.updateProduct(product, productId), HttpStatus.OK);
	}

	@DeleteMapping(value = "/deleteProduct/{productId}")
	public ResponseEntity<String> deleteProduct(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("productId") Integer productId) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(productService.deleteProduct(productId), HttpStatus.OK);
	}

	@GetMapping(value = "/allProducts")
	public ResponseEntity<List<JsonNode>> getAllProducts(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(productService.listAll(), HttpStatus.OK);
	}

	@GetMapping(value = "/allProducts/{categoryId}")
	public ResponseEntity<List<JsonNode>> getAllProducts(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("categoryId") Integer categoryId) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(productService.listAllByCategoryId(categoryId), HttpStatus.OK);
	}

	@PostMapping(value = "/storeImage/{productId}")
	public ResponseEntity<String> storeImage(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@RequestPart("file") MultipartFile file, @PathVariable("productId") Integer productId) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(imageService.addImage(file, productId), HttpStatus.OK);
	}

	@GetMapping(value = "/allImages")
	public ResponseEntity<List<JsonNode>> getAllImages(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(imageService.listAll(), HttpStatus.OK);
	}

}

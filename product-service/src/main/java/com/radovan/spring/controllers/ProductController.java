package com.radovan.spring.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
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
import com.radovan.spring.dto.ProductDto;
import com.radovan.spring.dto.ProductImageDto;
import com.radovan.spring.exceptions.DataNotValidatedException;
import com.radovan.spring.services.ProductImageService;
import com.radovan.spring.services.ProductService;

@RestController
@RequestMapping(value = "/products")
public class ProductController {

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductImageService imageService;

	private void setAuthorizationHeader(String authorizationHeader) {
		RequestContextHolder.getRequestAttributes().setAttribute("Authorization", authorizationHeader,
				RequestAttributes.SCOPE_REQUEST);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@PostMapping(value = "/createProduct")
	public ResponseEntity<String> createProduct(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@RequestBody @Validated ProductDto product, Errors errors) {
		setAuthorizationHeader(authorizationHeader);
		if (errors.hasErrors()) {
			throw new DataNotValidatedException(new Error("The product has not been validated!"));
		}

		ProductDto storedProduct = productService.addProduct(product);
		return new ResponseEntity<>("The product with id " + storedProduct.getProductId() + " has been stored!",
				HttpStatus.OK);
	}

	@GetMapping(value = "/productDetails/{productId}")
	public ResponseEntity<ProductDto> getProductDetails(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("productId") Integer productId) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(productService.getProductById(productId), HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@PutMapping(value = "/updateProduct/{productId}")
	public ResponseEntity<String> updateProduct(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@Validated @RequestBody ProductDto product, @PathVariable("productId") Integer productId, Errors errors) {
		setAuthorizationHeader(authorizationHeader);
		if (errors.hasErrors()) {
			throw new DataNotValidatedException(new Error("The product has not been validated!"));
		}
		ProductDto updatedProduct = productService.updateProduct(product, productId);
		return new ResponseEntity<>("The product with id " + updatedProduct.getProductId() + " has been updated!",
				HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ROLE_USER')")
	@PutMapping(value = "/orderUpdateProduct/{productId}")
	public ResponseEntity<Void> updateProduct(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@RequestHeader(value = "X-Source-Service", required = false) String sourceService,
			@Validated @RequestBody JsonNode product, @PathVariable("productId") Integer productId, Errors errors) {

		if (!"order-service".equals(sourceService)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
		}

		setAuthorizationHeader(authorizationHeader);
		if (errors.hasErrors()) {
			throw new DataNotValidatedException(new Error("The product has not been validated!"));
		}
		productService.updateProduct(product, productId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@DeleteMapping(value = "/deleteProduct/{productId}")
	public ResponseEntity<String> deleteProduct(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("productId") Integer productId) {
		setAuthorizationHeader(authorizationHeader);
		productService.deleteProduct(productId);
		return new ResponseEntity<>("The product with id " + productId + " has been permanently deleted!",
				HttpStatus.OK);
	}

	@GetMapping(value = "/allProducts")
	public ResponseEntity<List<ProductDto>> getAllProducts(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(productService.listAll(), HttpStatus.OK);
	}

	@GetMapping(value = "/allProducts/{categoryId}")
	public ResponseEntity<List<ProductDto>> getAllProducts(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("categoryId") Integer categoryId) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(productService.listAllByCategoryId(categoryId), HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@DeleteMapping(value = "/deleteProducts/{categoryId}")
	public ResponseEntity<Void> deleteProducts(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("categoryId") Integer categoryId) {
		setAuthorizationHeader(authorizationHeader);
		productService.deleteProductsByCategoryId(categoryId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@PostMapping(value = "/storeImage/{productId}")
	public ResponseEntity<String> storeImage(@RequestPart("file") MultipartFile file,
			@PathVariable("productId") Integer productId,
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		setAuthorizationHeader(authorizationHeader);
		imageService.addImage(file, productId);
		return ResponseEntity.ok().body("The image has been added without any issues!");
	}

	@GetMapping(value = "/allImages")
	public ResponseEntity<List<ProductImageDto>> getAllImages(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		setAuthorizationHeader(authorizationHeader);
		List<ProductImageDto> allImages = imageService.listAll();
		return ResponseEntity.ok().body(allImages);
	}
}

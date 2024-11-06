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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.fasterxml.jackson.databind.JsonNode;
import com.radovan.spring.services.ProductCategoryService;

@RestController
@RequestMapping(value = "/api/categories")
public class GatewayProductCategoryController {

	@Autowired
	private ProductCategoryService categoryService;

	private void setAuthorizationHeader(String authorizationHeader) {
		RequestContextHolder.getRequestAttributes().setAttribute("Authorization", authorizationHeader,
				RequestAttributes.SCOPE_REQUEST);
	}

	@PostMapping(value = "/addCategory")
	public ResponseEntity<String> createCategory(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@RequestBody JsonNode category) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(categoryService.addCategory(category), HttpStatus.OK);
	}

	@GetMapping(value = "/categoryDetails/{categoryId}")
	public ResponseEntity<JsonNode> getCategoryDetails(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("categoryId") Integer categoryId) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(categoryService.getCategoryById(categoryId), HttpStatus.OK);
	}

	@PutMapping(value = "/updateCategory/{categoryId}")
	public ResponseEntity<String> updateCategory(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("categoryId") Integer categoryId, @RequestBody JsonNode category) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(categoryService.updateCategory(category, categoryId), HttpStatus.OK);
	}

	@DeleteMapping(value = "/deleteCategory/{categoryId}")
	public ResponseEntity<String> deleteCategory(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("categoryId") Integer categoryId) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(categoryService.deleteCategory(categoryId), HttpStatus.OK);
	}

	@GetMapping(value = "/allCategories")
	public ResponseEntity<List<JsonNode>> getAllCategories(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(categoryService.listAll(), HttpStatus.OK);
	}

}

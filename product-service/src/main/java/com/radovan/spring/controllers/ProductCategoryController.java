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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.radovan.spring.dto.ProductCategoryDto;
import com.radovan.spring.exceptions.DataNotValidatedException;
import com.radovan.spring.services.ProductCategoryService;

@RestController
@RequestMapping(value = "/categories")
public class ProductCategoryController {

	@Autowired
	private ProductCategoryService categoryService;

	private void setAuthorizationHeader(String authorizationHeader) {
		RequestContextHolder.getRequestAttributes().setAttribute("Authorization", authorizationHeader,
				RequestAttributes.SCOPE_REQUEST);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@PostMapping(value = "/addCategory")
	public ResponseEntity<String> addCategory(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@RequestBody @Validated ProductCategoryDto category, Errors errors) {
		setAuthorizationHeader(authorizationHeader);
		if (errors.hasErrors()) {
			throw new DataNotValidatedException(new Error("The category has not been validated!"));
		}

		ProductCategoryDto storedCategory = categoryService.addCategory(category);
		return new ResponseEntity<>(
				"The category with id " + storedCategory.getProductCategoryId() + " has been stored!", HttpStatus.OK);
	}

	@GetMapping(value = "/categoryDetails/{categoryId}")
	public ResponseEntity<ProductCategoryDto> getCategoryDetails(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("categoryId") Integer categoryId) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(categoryService.getCategoryById(categoryId), HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@PutMapping(value = "/updateCategory/{categoryId}")
	public ResponseEntity<String> updateCategory(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@RequestBody @Validated ProductCategoryDto category, Errors errors,
			@PathVariable("categoryId") Integer categoryId) {
		setAuthorizationHeader(authorizationHeader);
		if (errors.hasErrors()) {
			throw new DataNotValidatedException(new Error("The category has not been validated!"));
		}

		ProductCategoryDto updatedCategory = categoryService.updateCategory(category, categoryId);
		return new ResponseEntity<>("The category with id " + updatedCategory.getProductCategoryId()
				+ " has been updated without any issues!", HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@DeleteMapping(value = "/deleteCategory/{categoryId}")
	public ResponseEntity<String> deleteCategory(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("categoryId") Integer categoryId) {
		setAuthorizationHeader(authorizationHeader);
		categoryService.deleteCategory(categoryId);
		return new ResponseEntity<>("The category with id " + categoryId + " has been permanently deleted!",
				HttpStatus.OK);
	}

	@GetMapping(value = "/allCategories")
	public ResponseEntity<List<ProductCategoryDto>> getAllCategories(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(categoryService.listAll(), HttpStatus.OK);
	}

}

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

import com.radovan.spring.dto.CustomerDto;
import com.radovan.spring.exceptions.DataNotValidatedException;
import com.radovan.spring.services.CustomerService;
import com.radovan.spring.utils.RegistrationForm;

@RestController
@RequestMapping(value = "/customers")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	private void setAuthorizationHeader(String authorizationHeader) {
		RequestContextHolder.getRequestAttributes().setAttribute("Authorization", authorizationHeader,
				RequestAttributes.SCOPE_REQUEST);
	}

	@PostMapping(value = "/createCustomer")
	public ResponseEntity<CustomerDto> createCustomer(@Validated @RequestBody RegistrationForm form, Errors errors,
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

		if (authorizationHeader != null) {
			// Ako je Authorization header prisutan, pretpostavljamo da je korisnik već
			// ulogovan
			return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
		}

		if (errors.hasErrors()) {
			throw new DataNotValidatedException(new Error("The data has not been validated!"));
		}

		return new ResponseEntity<>(customerService.addCustomer(form), HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@GetMapping(value = "/allCustomers")
	public ResponseEntity<List<CustomerDto>> getAllCustomers(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(customerService.listAll(), HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ROLE_USER')")
	@GetMapping(value = "/currentCustomer")
	public ResponseEntity<CustomerDto> getCurrentCustomer(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(customerService.getCurrentCustomer(), HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@DeleteMapping(value = "/deleteCustomer/{customerId}")
	public ResponseEntity<String> deleteCustomer(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("customerId") Integer customerId) {
		setAuthorizationHeader(authorizationHeader);
		customerService.removeCustomer(customerId);
		return new ResponseEntity<>("The customer with id: " + customerId + " has been permanently deleted!",
				HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@PutMapping(value = "/suspendCustomer/{customerId}")
	public ResponseEntity<String> suspendCustomer(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("customerId") Integer customerId) {
		setAuthorizationHeader(authorizationHeader);
		customerService.suspendCustomer(customerId);
		return new ResponseEntity<>("The customer with id " + customerId + " has been suspended", HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@PutMapping(value = "/reactivateCustomer/{customerId}")
	public ResponseEntity<String> reactivateCustomer(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("customerId") Integer customerId) {
		setAuthorizationHeader(authorizationHeader);
		customerService.reactivateCustomer(customerId);
		return new ResponseEntity<>("The customer with id " + customerId + " has been reactivated!", HttpStatus.OK);
	}

}

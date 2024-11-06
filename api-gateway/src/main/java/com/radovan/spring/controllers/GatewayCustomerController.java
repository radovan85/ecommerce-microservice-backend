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
import com.radovan.spring.services.CustomerService;
import com.radovan.spring.services.ShippingAddressService;

@RestController
@RequestMapping(value = "/api/customers")
public class GatewayCustomerController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private ShippingAddressService addressService;

	private void setAuthorizationHeader(String authorizationHeader) {
		RequestContextHolder.getRequestAttributes().setAttribute("Authorization", authorizationHeader,
				RequestAttributes.SCOPE_REQUEST);
	}

	@PostMapping(value = "/createCustomer")
	public ResponseEntity<JsonNode> createCustomer(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@RequestBody JsonNode form) {
		return new ResponseEntity<>(customerService.addCustomer(form), HttpStatus.OK);
	}

	@GetMapping(value = "/allCustomers")
	public ResponseEntity<List<JsonNode>> getAllCustomers(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(customerService.listAll(), HttpStatus.OK);
	}

	@DeleteMapping(value = "/deleteCustomer/{customerId}")
	public ResponseEntity<String> deleteCustomer(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("customerId") Integer customerId) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(customerService.deleteCustomer(customerId), HttpStatus.OK);
	}

	@PutMapping(value = "/updateAddress")
	public ResponseEntity<String> updateAddress(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@RequestBody JsonNode address) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(addressService.updateAddress(address), HttpStatus.OK);
	}

	@PutMapping(value = "/suspendCustomer/{customerId}")
	public ResponseEntity<String> suspendCustomer(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("customerId") Integer customerId) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(customerService.suspendCustomer(customerId), HttpStatus.OK);
	}

	@RequestMapping(value = "/reactivateCustomer/{customerId}")
	public ResponseEntity<String> reactivateCustomer(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("customerId") Integer customerId) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(customerService.reactivateCustomer(customerId), HttpStatus.OK);
	}

}

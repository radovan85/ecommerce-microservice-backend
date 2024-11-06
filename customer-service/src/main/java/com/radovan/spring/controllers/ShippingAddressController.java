package com.radovan.spring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.radovan.spring.dto.ShippingAddressDto;
import com.radovan.spring.exceptions.DataNotValidatedException;
import com.radovan.spring.services.ShippingAddressService;

@RestController
@RequestMapping(value = "/addresses")
public class ShippingAddressController {

	@Autowired
	private ShippingAddressService addressService;

	private void setAuthorizationHeader(String authorizationHeader) {
		RequestContextHolder.getRequestAttributes().setAttribute("Authorization", authorizationHeader,
				RequestAttributes.SCOPE_REQUEST);
	}

	@PreAuthorize(value = "hasAuthority('ROLE_USER')")
	@PutMapping(value = "/updateAddress")
	public ResponseEntity<String> updateAddress(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@RequestBody @Validated ShippingAddressDto address, Errors errors) {
		setAuthorizationHeader(authorizationHeader);
		if (errors.hasErrors()) {
			throw new DataNotValidatedException(new Error("The address has not been validated!"));
		}

		addressService.updateAddress(address);
		return new ResponseEntity<String>("Your shipping address has been updated!", HttpStatus.OK);
	}

	@GetMapping(value = "/addressDetails/{addressId}")
	public ResponseEntity<ShippingAddressDto> getAddressDetails(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("addressId") Integer addressId) {
		return new ResponseEntity<>(addressService.getAddressById(addressId), HttpStatus.OK);
	}
}

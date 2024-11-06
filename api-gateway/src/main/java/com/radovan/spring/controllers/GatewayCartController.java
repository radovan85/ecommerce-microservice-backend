package com.radovan.spring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.fasterxml.jackson.databind.JsonNode;
import com.radovan.spring.services.CartItemService;
import com.radovan.spring.services.CartService;

@RestController
@RequestMapping(value = "/api/cart")
public class GatewayCartController {

	@Autowired
	private CartService cartService;

	@Autowired
	private CartItemService cartItemService;

	private void setAuthorizationHeader(String authorizationHeader) {
		RequestContextHolder.getRequestAttributes().setAttribute("Authorization", authorizationHeader,
				RequestAttributes.SCOPE_REQUEST);
	}

	@GetMapping(value = "/getMyCart")
	public ResponseEntity<JsonNode> getMyCart(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(cartService.getMyCart(), HttpStatus.OK);
	}

	@DeleteMapping(value = "/clearCart")
	private ResponseEntity<String> clearMyCart(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(cartService.clearCart(), HttpStatus.OK);
	}

	@PostMapping(value = "/addItem/{productId}")
	private ResponseEntity<String> addCartItem(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("productId") Integer productId) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(cartItemService.addCartItem(productId), HttpStatus.OK);
	}

	@DeleteMapping(value = "/deleteItem/{itemId}")
	private ResponseEntity<String> deleteItem(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("itemId") Integer itemId) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(cartItemService.deleteItem(itemId), HttpStatus.OK);
	}
}

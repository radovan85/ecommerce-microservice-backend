package com.radovan.spring.controllers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.radovan.spring.dto.CartItemDto;
import com.radovan.spring.services.CartItemService;

@RestController
@RequestMapping(value = "/items")
public class CartItemController {

	@Autowired
	private CartItemService cartItemService;

	private void setAuthorizationHeader(String authorizationHeader) {
		RequestContextHolder.getRequestAttributes().setAttribute("Authorization", authorizationHeader,
				RequestAttributes.SCOPE_REQUEST);
	}

	private List<String> serviceList = Stream.of("order-service", "customer-service", "cart-service", "product-service")
			.collect(Collectors.toList());

	@PreAuthorize(value = "hasAuthority('ROLE_USER')")
	@PostMapping(value = "/addItem/{productId}")
	public ResponseEntity<String> addCartItem(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("productId") Integer productId) {
		setAuthorizationHeader(authorizationHeader);
		cartItemService.addCartItem(productId);
		return new ResponseEntity<>("The item has been added to the cart!", HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ROLE_USER')")
	@DeleteMapping(value = "/deleteItem/{itemId}")
	public ResponseEntity<String> deleteItem(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("itemId") Integer itemId) {
		setAuthorizationHeader(authorizationHeader);
		cartItemService.removeCartItem(itemId);
		return new ResponseEntity<>("The item has been removed from the cart!", HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@DeleteMapping(value = "/clearAllByProductId/{productId}")
	public ResponseEntity<Void> deleteAllByProductId(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("productId") Integer productId) {
		setAuthorizationHeader(authorizationHeader);
		cartItemService.removeAllByProductId(productId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@DeleteMapping(value = "/clearAllByCartId/{cartId}")
	public ResponseEntity<Void> deleteAllByCartId(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("cartId") Integer cartId) {
		setAuthorizationHeader(authorizationHeader);
		cartItemService.removeAllByCartId(cartId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping(value = "/allItemsByCartId/{cartId}")
	public ResponseEntity<List<CartItemDto>> findAllItemsByCartId(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("cartId") Integer cartId) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(cartItemService.listAllByCartId(cartId), HttpStatus.OK);
	}

	@GetMapping(value = "/allItemsByProductId/{productId}")
	public ResponseEntity<List<CartItemDto>> findAllItemsByProductId(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("productId") Integer productId) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(cartItemService.listAllByProductId(productId), HttpStatus.OK);
	}

	@GetMapping(value = "/itemDetails/{itemId}")
	public ResponseEntity<CartItemDto> getItemDetails(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@RequestHeader(value = "X-Source-Service", required = false) String sourceService,
			@PathVariable("itemId") Integer itemId) {
		if (!serviceList.contains(sourceService)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(cartItemService.getItemById(itemId), HttpStatus.OK);
	}

	@PutMapping(value = "/updateItem/{itemId}")
	public ResponseEntity<Void> updateItem(
			@RequestHeader(value = "X-Source-Service", required = false) String sourceService,
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("itemId") Integer itemId, @RequestBody JsonNode item) {
		if (!"product-service".equals(sourceService)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
		}
		setAuthorizationHeader(authorizationHeader);
		cartItemService.updateItem(itemId, item);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}

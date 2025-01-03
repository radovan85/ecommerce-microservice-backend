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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.radovan.spring.dto.CartDto;
import com.radovan.spring.services.CartService;

@RestController
@RequestMapping(value = "/cart")
public class CartController {

	@Autowired
	private CartService cartService;

	private void setAuthorizationHeader(String authorizationHeader) {
		RequestContextHolder.getRequestAttributes().setAttribute("Authorization", authorizationHeader,
				RequestAttributes.SCOPE_REQUEST);
	}

	private List<String> serviceList = Stream.of("order-service", "customer-service", "cart-service", "product-service")
			.collect(Collectors.toList());

	@PostMapping(value = "/addCart")
	public ResponseEntity<CartDto> addCart(
			@RequestHeader(value = "X-Source-Service", required = false) String sourceService) {

		// Proverite da li je poziv došao iz "customer-service"
		if (!"customer-service".equals(sourceService)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
		}

		return new ResponseEntity<>(cartService.addCart(), HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ROLE_USER')")
	@GetMapping(value = "/getMyCart")
	public ResponseEntity<CartDto> getMyCart(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(cartService.getMyCart(), HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ROLE_USER')")
	@GetMapping(value = "/validateCart/{cartId}")
	public ResponseEntity<CartDto> validateCart(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@RequestHeader(value = "X-Source-Service", required = false) String sourceService,
			@PathVariable("cartId") Integer cartId) {
		if (!"order-service".equals(sourceService)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
		}
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(cartService.validateCart(cartId), HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ROLE_USER')")
	@GetMapping(value = "/calculateCartPrice/{cartId}")
	public ResponseEntity<Float> getCartPrice(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("cartId") Integer cartId) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(cartService.calculateGrandTotal(cartId), HttpStatus.OK);
	}

	@GetMapping(value = "/refreshCartState/{cartId}")
	public ResponseEntity<Void> refreshCartState(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@RequestHeader(value = "X-Source-Service", required = false) String sourceService,
			@PathVariable("cartId") Integer cartId) {
		if (!serviceList.contains(sourceService)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		setAuthorizationHeader(authorizationHeader);
		cartService.refreshCartState(cartId);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping(value = "/refreshAllCarts")
	public ResponseEntity<Void> refreshAllCarts(
			@RequestHeader(value = "X-Source-Service", required = false) String sourceService,
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

		setAuthorizationHeader(authorizationHeader);
		if (!"product-service".equals(sourceService)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
		}
		cartService.refreshAllCarts();
		return new ResponseEntity<>(HttpStatus.OK);

	}

	@PreAuthorize(value = "hasAuthority('ROLE_USER')")
	@DeleteMapping(value = "/clearCart")
	public ResponseEntity<String> clearMyCart(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		setAuthorizationHeader(authorizationHeader);
		cartService.clearCart();
		return new ResponseEntity<>("All items from your cart have been removed!", HttpStatus.OK);
	}

	@DeleteMapping(value = "/deleteCart/{cartId}")
	public ResponseEntity<Void> deleteCart(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@RequestHeader(value = "X-Source-Service", required = false) String sourceService,
			@PathVariable("cartId") Integer cartId) {
		if (!"customer-service".equals(sourceService)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
		}

		setAuthorizationHeader(authorizationHeader);
		cartService.removeCart(cartId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}

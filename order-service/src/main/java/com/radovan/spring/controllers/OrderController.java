package com.radovan.spring.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.radovan.spring.dto.OrderAddressDto;
import com.radovan.spring.dto.OrderDto;
import com.radovan.spring.dto.OrderItemDto;
import com.radovan.spring.services.OrderAddressService;
import com.radovan.spring.services.OrderItemService;
import com.radovan.spring.services.OrderService;

@RestController
@RequestMapping(value = "/order")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderAddressService orderAddressService;

	@Autowired
	private OrderItemService orderItemService;

	private void setAuthorizationHeader(String authorizationHeader) {
		RequestContextHolder.getRequestAttributes().setAttribute("Authorization", authorizationHeader,
				RequestAttributes.SCOPE_REQUEST);
	}

	@PreAuthorize(value = "hasAuthority('ROLE_USER')")
	@PostMapping(value = "/placeOrder")
	public ResponseEntity<String> placeOrder(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		setAuthorizationHeader(authorizationHeader);
		orderService.addOrder();
		return new ResponseEntity<String>("Your order has been submitted without any problems.", HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@GetMapping(value = "/allOrders")
	public ResponseEntity<List<OrderDto>> getAllOrders(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		setAuthorizationHeader(authorizationHeader);
		List<OrderDto> allOrders = orderService.listAll();
		return new ResponseEntity<>(allOrders, HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@GetMapping(value = "/orderDetails/{orderId}")
	public ResponseEntity<OrderDto> orderDetails(@PathVariable("orderId") Integer orderId,
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		setAuthorizationHeader(authorizationHeader);
		OrderDto order = orderService.getOrderById(orderId);
		return new ResponseEntity<>(order, HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@GetMapping(value = "/allAddresses")
	public ResponseEntity<List<OrderAddressDto>> getAllAddresses(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		setAuthorizationHeader(authorizationHeader);
		List<OrderAddressDto> allAddresses = orderAddressService.listAll();
		return new ResponseEntity<>(allAddresses, HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@GetMapping(value = "/allItems/{orderId}")
	public ResponseEntity<List<OrderItemDto>> getAllItems(@PathVariable("orderId") Integer orderId,
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

		setAuthorizationHeader(authorizationHeader);
		List<OrderItemDto> allItems = orderItemService.listAllByOrderId(orderId);
		return new ResponseEntity<>(allItems, HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@DeleteMapping(value = "/deleteOrder/{orderId}")
	public ResponseEntity<String> deleteOrder(@PathVariable("orderId") Integer orderId,
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader

	) {

		setAuthorizationHeader(authorizationHeader);
		orderService.deleteOrder(orderId);
		return new ResponseEntity<String>("The order with id " + orderId + " has been permanently deleted!",
				HttpStatus.OK);
	}

	@DeleteMapping(value = "/deleteAllByCartId/{cartId}")
	public ResponseEntity<Void> deleteAllOrders(@PathVariable("cartId") Integer cartId,
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@RequestHeader(value = "X-Source-Service", required = false) String sourceService) {

		if (!"customer-service".equals(sourceService)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
		}

		setAuthorizationHeader(authorizationHeader);
		orderService.deleteAllByCartId(cartId);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
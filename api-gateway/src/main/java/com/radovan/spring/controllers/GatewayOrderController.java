package com.radovan.spring.controllers;

import java.util.List;

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
import com.radovan.spring.services.OrderAddressService;
import com.radovan.spring.services.OrderItemService;
import com.radovan.spring.services.OrderService;

@RestController
@RequestMapping(value = "/api/order")
public class GatewayOrderController {

	private OrderService orderService;
	private OrderAddressService addressService;
	private OrderItemService itemService;

	@Autowired
	private void initialize(OrderService orderService, OrderAddressService addressService,
			OrderItemService itemService) {
		this.orderService = orderService;
		this.addressService = addressService;
		this.itemService = itemService;
	}

	private void setAuthorizationHeader(String authorizationHeader) {
		RequestContextHolder.getRequestAttributes().setAttribute("Authorization", authorizationHeader,
				RequestAttributes.SCOPE_REQUEST);
	}

	@PostMapping(value = "/placeOrder")
	public ResponseEntity<String> placeMyOrder(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(orderService.addOrder(), HttpStatus.OK);
	}

	@GetMapping(value = "/allOrders")
	public ResponseEntity<List<JsonNode>> getAllOrders(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(orderService.listAll(), HttpStatus.OK);
	}

	@GetMapping(value = "/orderDetails/{orderId}")
	public ResponseEntity<JsonNode> getOrderDetails(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("orderId") Integer orderId) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(orderService.getOrderById(orderId), HttpStatus.OK);
	}

	@DeleteMapping(value = "/deleteOrder/{orderId}")
	public ResponseEntity<String> deleteOrder(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("orderId") Integer orderId) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(orderService.deleteOrder(orderId), HttpStatus.OK);
	}

	@GetMapping(value = "/allAddresses")
	public ResponseEntity<List<JsonNode>> getAllAddresses(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(addressService.listAll(), HttpStatus.OK);
	}

	@GetMapping(value = "/allItems/{orderId}")
	public ResponseEntity<List<JsonNode>> allItemsByOrderId(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("orderId") Integer orderId) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(itemService.listAllByOrderId(orderId), HttpStatus.OK);
	}

}

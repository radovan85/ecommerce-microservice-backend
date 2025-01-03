package com.radovan.spring.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.radovan.spring.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class GatewayUserController {

	@Autowired
	private UserService userService;

	private void setAuthorizationHeader(String authorizationHeader) {
		RequestContextHolder.getRequestAttributes().setAttribute("Authorization", authorizationHeader,
				RequestAttributes.SCOPE_REQUEST);
	}

	@PostMapping(value = "/login")
	public ResponseEntity<JsonNode> createAuthenticationToken(@RequestBody JsonNode authRequest,
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

		if (authorizationHeader != null) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		return new ResponseEntity<>(userService.authenticateUser(authRequest), HttpStatus.OK);
	}

	@GetMapping(value = "/allUsers")
	public ResponseEntity<List<JsonNode>> getAllUsers(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(userService.listAll(), HttpStatus.OK);
	}

	@GetMapping(value = "/currentUser")
	public ResponseEntity<JsonNode> getCurrentUser(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		setAuthorizationHeader(authorizationHeader);
		return new ResponseEntity<>(userService.getCurrentUser(), HttpStatus.OK);
	}

	@GetMapping(value = "/userDetails/{userId}")
	public ResponseEntity<JsonNode> getUserDetails(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable("userId") Integer userId) {
		return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.OK);
	}

}

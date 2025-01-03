package com.radovan.spring.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GatewayMainController {

	@GetMapping
	public ResponseEntity<String> entryPoint() {
		return new ResponseEntity<String>("Api Gateway endpoint", HttpStatus.OK);
	}
}

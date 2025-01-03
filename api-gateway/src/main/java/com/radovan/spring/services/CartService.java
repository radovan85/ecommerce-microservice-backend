package com.radovan.spring.services;

import com.fasterxml.jackson.databind.JsonNode;

public interface CartService {

	JsonNode getMyCart();
	
	String clearCart();
	
}

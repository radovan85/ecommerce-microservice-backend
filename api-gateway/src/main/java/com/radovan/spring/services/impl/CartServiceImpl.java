package com.radovan.spring.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.radovan.spring.converter.DeserializeConverter;
import com.radovan.spring.services.CartService;

@Service
public class CartServiceImpl implements CartService {

	private final String CART_SERVICE_URL = "http://localhost:8083/cart";

	@Autowired
	private DeserializeConverter deserializeConverter;

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public JsonNode getMyCart() {
		// TODO Auto-generated method stub
		String cartUrl = CART_SERVICE_URL + "/getMyCart";
		ResponseEntity<JsonNode> cartResponse = deserializeConverter.getJsonNodeResponse(cartUrl);
		return cartResponse.getBody();
	}

	@Override
	public String clearCart() {
		// TODO Auto-generated method stub
		String clearCartUrl = CART_SERVICE_URL + "/clearCart";
		ResponseEntity<String> response = restTemplate.exchange(clearCartUrl, HttpMethod.DELETE, null, String.class);
		return response.getBody();
	}

}

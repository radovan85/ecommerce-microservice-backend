package com.radovan.spring.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.radovan.spring.services.CartItemService;

@Service
public class CartItemServiceImpl implements CartItemService {

	@Autowired
	private RestTemplate restTemplate;

	private final String CART_SERVICE_URL = "http://localhost:8083/items";

	@Override
	public String addCartItem(Integer productId) {
		// TODO Auto-generated method stub
		String cartItemUrl = CART_SERVICE_URL + "/addItem/" + productId;
		ResponseEntity<String> response = restTemplate.exchange(cartItemUrl, HttpMethod.POST, null, String.class);
		return response.getBody();
	}

	@Override
	public String deleteItem(Integer itemId) {
		// TODO Auto-generated method stub
		String deleteItemUrl = CART_SERVICE_URL + "/deleteItem/" + itemId;
		ResponseEntity<String> response = restTemplate.exchange(deleteItemUrl, HttpMethod.DELETE, null, String.class);
		return response.getBody();
	}

}

package com.radovan.spring.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.radovan.spring.services.CartItemService;
import com.radovan.spring.utils.ServiceUrlProvider;

@Service
public class CartItemServiceImpl implements CartItemService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ServiceUrlProvider urlProvider;

	@Override
	public String addCartItem(Integer productId) {
		// TODO Auto-generated method stub
		String cartItemUrl = urlProvider.getCartServiceUrl() + "/items/addItem/" + productId;
		ResponseEntity<String> response = restTemplate.exchange(cartItemUrl, HttpMethod.POST, null, String.class);
		return response.getBody();
	}

	@Override
	public String deleteItem(Integer itemId) {
		// TODO Auto-generated method stub
		String deleteItemUrl = urlProvider.getCartServiceUrl() + "/items/deleteItem/" + itemId;
		ResponseEntity<String> response = restTemplate.exchange(deleteItemUrl, HttpMethod.DELETE, null, String.class);
		return response.getBody();
	}

}

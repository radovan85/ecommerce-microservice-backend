package com.radovan.spring.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.radovan.spring.services.ShippingAddressService;

@Service
public class ShippingAddressServiceImpl implements ShippingAddressService {

	private final String CUSTOMER_SERVICE_URL = "http://localhost:8082/addresses";

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public String updateAddress(JsonNode address) {
		// TODO Auto-generated method stub
		String addressUrl = CUSTOMER_SERVICE_URL + "/updateAddress";
		HttpEntity<JsonNode> requestEntity = new HttpEntity<>(address);
		ResponseEntity<String> response = restTemplate.exchange(addressUrl, HttpMethod.PUT, requestEntity,
				String.class);
		return response.getBody();
	}

}

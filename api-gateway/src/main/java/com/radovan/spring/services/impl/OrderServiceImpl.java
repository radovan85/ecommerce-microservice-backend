package com.radovan.spring.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.radovan.spring.converter.DeserializeConverter;
import com.radovan.spring.services.OrderService;
import com.radovan.spring.utils.ServiceUrlProvider;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private DeserializeConverter deserializeConverter;

	@Autowired
	private ServiceUrlProvider urlProvider;

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public String addOrder() {
		// TODO Auto-generated method stub
		String url = urlProvider.getOrderServiceUrl() + "/order/placeOrder";
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, null, String.class);
		return response.getBody();
	}

	@Override
	public List<JsonNode> listAll() {
		// TODO Auto-generated method stub
		String url = urlProvider.getOrderServiceUrl() + "/order/allOrders";
		return deserializeConverter.getJsonNodeList(url);
	}

	@Override
	public JsonNode getOrderById(Integer orderId) {
		// TODO Auto-generated method stub
		String url = urlProvider.getOrderServiceUrl() + "/order/orderDetails/" + orderId;
		ResponseEntity<JsonNode> response = deserializeConverter.getJsonNodeResponse(url);
		return response.getBody();
	}

	@Override
	public String deleteOrder(Integer orderId) {
		// TODO Auto-generated method stub
		String url = urlProvider.getOrderServiceUrl() + "/order/deleteOrder/" + orderId;
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);
		return response.getBody();
	}

}

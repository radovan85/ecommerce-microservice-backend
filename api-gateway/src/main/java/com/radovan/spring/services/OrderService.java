package com.radovan.spring.services;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public interface OrderService {

	String addOrder();

	List<JsonNode> listAll();

	JsonNode getOrderById(Integer orderId);

	String deleteOrder(Integer orderId);
}

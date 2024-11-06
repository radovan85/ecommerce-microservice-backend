package com.radovan.spring.services;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public interface OrderItemService {

	List<JsonNode> listAllByOrderId(Integer orderId);
}
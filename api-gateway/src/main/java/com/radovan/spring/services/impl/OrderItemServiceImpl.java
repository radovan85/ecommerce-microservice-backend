package com.radovan.spring.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.radovan.spring.converter.DeserializeConverter;
import com.radovan.spring.services.OrderItemService;

@Service
public class OrderItemServiceImpl implements OrderItemService {

	@Autowired
	private DeserializeConverter deserializeConverter;

	private final String ORDER_SERVICE_URL = "http://localhost:8085/order";

	@Override
	public List<JsonNode> listAllByOrderId(Integer orderId) {
		// TODO Auto-generated method stub
		String url = ORDER_SERVICE_URL + "/allItems/" + orderId;
		return deserializeConverter.getJsonNodeList(url);
	}

}

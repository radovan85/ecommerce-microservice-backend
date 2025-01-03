package com.radovan.spring.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.radovan.spring.converter.DeserializeConverter;
import com.radovan.spring.services.OrderItemService;
import com.radovan.spring.utils.ServiceUrlProvider;

@Service
public class OrderItemServiceImpl implements OrderItemService {

	@Autowired
	private DeserializeConverter deserializeConverter;

	@Autowired
	private ServiceUrlProvider urlProvider;

	@Override
	public List<JsonNode> listAllByOrderId(Integer orderId) {
		// TODO Auto-generated method stub
		String url = urlProvider.getOrderServiceUrl() + "/order/allItems/" + orderId;
		return deserializeConverter.getJsonNodeList(url);
	}

}

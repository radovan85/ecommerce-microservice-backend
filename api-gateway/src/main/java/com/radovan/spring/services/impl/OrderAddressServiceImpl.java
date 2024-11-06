package com.radovan.spring.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.radovan.spring.converter.DeserializeConverter;
import com.radovan.spring.services.OrderAddressService;

@Service
public class OrderAddressServiceImpl implements OrderAddressService {

	@Autowired
	private DeserializeConverter deserializeConverter;

	private final String ORDER_SERVICE_URL = "http://localhost:8085/order";

	@Override
	public List<JsonNode> listAll() {
		// TODO Auto-generated method stub
		String url = ORDER_SERVICE_URL + "/allAddresses";
		return deserializeConverter.getJsonNodeList(url);
	}

}

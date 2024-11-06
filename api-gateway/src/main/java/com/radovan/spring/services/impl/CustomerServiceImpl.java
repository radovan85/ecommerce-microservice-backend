package com.radovan.spring.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.radovan.spring.converter.DeserializeConverter;
import com.radovan.spring.services.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {

	private final String CUSTOMER_SERVICE_URL = "http://localhost:8082/customers";

	@Autowired
	private DeserializeConverter deserializeConverter;

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public JsonNode addCustomer(JsonNode registrationForm) {
		// TODO Auto-generated method stub
		String customerUrl = CUSTOMER_SERVICE_URL + "/createCustomer";
		HttpEntity<JsonNode> requestEntity = new HttpEntity<JsonNode>(registrationForm);
		ResponseEntity<JsonNode> response = restTemplate.exchange(customerUrl, HttpMethod.POST, requestEntity,
				JsonNode.class);
		return response.getBody();
	}

	@Override
	public List<JsonNode> listAll() {
		// TODO Auto-generated method stub
		String allCustomersUrl = CUSTOMER_SERVICE_URL + "/allCustomers";
		return deserializeConverter.getJsonNodeList(allCustomersUrl);
	}

	@Override
	public String deleteCustomer(Integer customerId) {
		// TODO Auto-generated method stub
		String deleteCustomerUrl = CUSTOMER_SERVICE_URL + "/deleteCustomer/" + customerId;
		ResponseEntity<String> response = restTemplate.exchange(deleteCustomerUrl, HttpMethod.DELETE, null,
				String.class);
		return response.getBody();
	}

	@Override
	public String suspendCustomer(Integer customerId) {
		// TODO Auto-generated method stub
		String url = CUSTOMER_SERVICE_URL + "/suspendCustomer/" + customerId;
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, null, String.class);
		return response.getBody();
	}

	@Override
	public String reactivateCustomer(Integer customerId) {
		// TODO Auto-generated method stub
		String url = CUSTOMER_SERVICE_URL + "/reactivateCustomer/" + customerId;
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, null, String.class);
		return response.getBody();
	}

}

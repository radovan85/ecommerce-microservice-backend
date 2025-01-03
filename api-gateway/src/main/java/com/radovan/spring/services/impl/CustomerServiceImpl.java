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
import com.radovan.spring.utils.ServiceUrlProvider;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private DeserializeConverter deserializeConverter;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ServiceUrlProvider urlProvider;

	@Override
	public JsonNode addCustomer(JsonNode registrationForm) {
		// TODO Auto-generated method stub
		String customerUrl = urlProvider.getCustomerServiceUrl() + "/customers/createCustomer";
		HttpEntity<JsonNode> requestEntity = new HttpEntity<JsonNode>(registrationForm);
		ResponseEntity<JsonNode> response = restTemplate.exchange(customerUrl, HttpMethod.POST, requestEntity,
				JsonNode.class);
		return response.getBody();
	}

	@Override
	public List<JsonNode> listAll() {
		// TODO Auto-generated method stub
		String allCustomersUrl = urlProvider.getCustomerServiceUrl() + "/customers/allCustomers";
		return deserializeConverter.getJsonNodeList(allCustomersUrl);
	}

	@Override
	public String deleteCustomer(Integer customerId) {
		// TODO Auto-generated method stub
		String deleteCustomerUrl = urlProvider.getCustomerServiceUrl() + "/customers/deleteCustomer/" + customerId;
		ResponseEntity<String> response = restTemplate.exchange(deleteCustomerUrl, HttpMethod.DELETE, null,
				String.class);
		return response.getBody();
	}

	@Override
	public String suspendCustomer(Integer customerId) {
		// TODO Auto-generated method stub
		String url = urlProvider.getCustomerServiceUrl() + "/customers/suspendCustomer/" + customerId;
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, null, String.class);
		return response.getBody();
	}

	@Override
	public String reactivateCustomer(Integer customerId) {
		// TODO Auto-generated method stub
		String url = urlProvider.getCustomerServiceUrl() + "/customers/reactivateCustomer/" + customerId;
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, null, String.class);
		return response.getBody();
	}

}

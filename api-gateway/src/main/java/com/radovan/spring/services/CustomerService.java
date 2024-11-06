package com.radovan.spring.services;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public interface CustomerService {

	JsonNode addCustomer(JsonNode registrationForm);

	List<JsonNode> listAll();

	String deleteCustomer(Integer customerId);

	String suspendCustomer(Integer customerId);

	String reactivateCustomer(Integer customerId);
}

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
import com.radovan.spring.services.UserService;
import com.radovan.spring.utils.ServiceUrlProvider;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private DeserializeConverter deserializeConverter;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ServiceUrlProvider urlProvider;

	@Override
	public JsonNode authenticateUser(JsonNode authRequest) {
		// TODO Auto-generated method stub
		String url = urlProvider.getUserServiceUrl() + "/login";
		HttpEntity<JsonNode> requestEntity = new HttpEntity<JsonNode>(authRequest);
		ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, JsonNode.class);
		return response.getBody();
	}

	@Override
	public List<JsonNode> listAll() {
		// TODO Auto-generated method stub
		String url = urlProvider.getUserServiceUrl() + "/allUsers";
		return deserializeConverter.getJsonNodeList(url);
	}

	@Override
	public JsonNode getCurrentUser() {
		// TODO Auto-generated method stub
		String url = urlProvider.getUserServiceUrl() + "/currentUser";
		ResponseEntity<JsonNode> response = deserializeConverter.getJsonNodeResponse(url);
		return response.getBody();
	}

	@Override
	public JsonNode getUserById(Integer userId) {
		// TODO Auto-generated method stub
		String url = urlProvider.getUserServiceUrl() + "/userDetails/" + userId;
		ResponseEntity<JsonNode> response = deserializeConverter.getJsonNodeResponse(url);
		return response.getBody();
	}

}

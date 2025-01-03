package com.radovan.spring.services;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public interface UserService {

	JsonNode authenticateUser(JsonNode authRequest);

	List<JsonNode> listAll();

	JsonNode getCurrentUser();

	JsonNode getUserById(Integer userId);
}

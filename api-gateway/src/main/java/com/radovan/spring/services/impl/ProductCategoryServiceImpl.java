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
import com.radovan.spring.services.ProductCategoryService;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

	private final String PRODUCT_SERVICE_URL = "http://localhost:8084/categories";

	@Autowired
	private DeserializeConverter deserializeConverter;

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public String addCategory(JsonNode category) {
		// TODO Auto-generated method stub
		String categoryUrl = PRODUCT_SERVICE_URL + "/addCategory";
		HttpEntity<JsonNode> requestEntity = new HttpEntity<>(category);
		ResponseEntity<String> response = restTemplate.exchange(categoryUrl, HttpMethod.POST, requestEntity,
				String.class);
		return response.getBody();
	}

	@Override
	public JsonNode getCategoryById(Integer categoryId) {
		// TODO Auto-generated method stub
		String categoryUrl = PRODUCT_SERVICE_URL + "/categoryDetails/" + categoryId;
		ResponseEntity<JsonNode> response = deserializeConverter.getJsonNodeResponse(categoryUrl);
		return response.getBody();
	}

	@Override
	public String updateCategory(JsonNode category, Integer categoryId) {
		// TODO Auto-generated method stub
		String categoryUrl = PRODUCT_SERVICE_URL + "/updateCategory/" + categoryId;
		HttpEntity<JsonNode> requestEntity = new HttpEntity<JsonNode>(category);
		ResponseEntity<String> response = restTemplate.exchange(categoryUrl, HttpMethod.PUT, requestEntity,
				String.class);
		return response.getBody();
	}

	@Override
	public String deleteCategory(Integer categoryId) {
		// TODO Auto-generated method stub
		String categoryUrl = PRODUCT_SERVICE_URL + "/deleteCategory/" + categoryId;
		ResponseEntity<String> response = restTemplate.exchange(categoryUrl, HttpMethod.DELETE, null, String.class);
		return response.getBody();
	}

	@Override
	public List<JsonNode> listAll() {
		// TODO Auto-generated method stub
		String categoriesUrl = PRODUCT_SERVICE_URL + "/allCategories";
		return deserializeConverter.getJsonNodeList(categoriesUrl);
	}

}

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
import com.radovan.spring.utils.ServiceUrlProvider;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

	@Autowired
	private DeserializeConverter deserializeConverter;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ServiceUrlProvider urlProvider;

	@Override
	public String addCategory(JsonNode category) {
		// TODO Auto-generated method stub
		String categoryUrl = urlProvider.getProductServiceUrl() + "/categories/addCategory";
		HttpEntity<JsonNode> requestEntity = new HttpEntity<>(category);
		ResponseEntity<String> response = restTemplate.exchange(categoryUrl, HttpMethod.POST, requestEntity,
				String.class);
		return response.getBody();
	}

	@Override
	public JsonNode getCategoryById(Integer categoryId) {
		// TODO Auto-generated method stub
		String categoryUrl = urlProvider.getProductServiceUrl() + "/categories/categoryDetails/" + categoryId;
		ResponseEntity<JsonNode> response = deserializeConverter.getJsonNodeResponse(categoryUrl);
		return response.getBody();
	}

	@Override
	public String updateCategory(JsonNode category, Integer categoryId) {
		// TODO Auto-generated method stub
		String categoryUrl = urlProvider.getProductServiceUrl() + "/categories/updateCategory/" + categoryId;
		HttpEntity<JsonNode> requestEntity = new HttpEntity<JsonNode>(category);
		ResponseEntity<String> response = restTemplate.exchange(categoryUrl, HttpMethod.PUT, requestEntity,
				String.class);
		return response.getBody();
	}

	@Override
	public String deleteCategory(Integer categoryId) {
		// TODO Auto-generated method stub
		String categoryUrl = urlProvider.getProductServiceUrl() + "/categories/deleteCategory/" + categoryId;
		ResponseEntity<String> response = restTemplate.exchange(categoryUrl, HttpMethod.DELETE, null, String.class);
		return response.getBody();
	}

	@Override
	public List<JsonNode> listAll() {
		// TODO Auto-generated method stub
		String categoriesUrl = urlProvider.getProductServiceUrl() + "/categories/allCategories";
		return deserializeConverter.getJsonNodeList(categoriesUrl);
	}

}

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
import com.radovan.spring.services.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	private final String PRODUCT_SERVICE_URL = "http://localhost:8084/products";

	@Autowired
	private DeserializeConverter deserializeConverter;

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public String createProduct(JsonNode product) {
		// TODO Auto-generated method stub
		String url = PRODUCT_SERVICE_URL + "/createProduct";
		HttpEntity<JsonNode> requestEntity = new HttpEntity<JsonNode>(product);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
		return response.getBody();
	}

	@Override
	public JsonNode getBroductById(Integer productId) {
		// TODO Auto-generated method stub
		String url = PRODUCT_SERVICE_URL + "/productDetails/" + productId;
		ResponseEntity<JsonNode> response = deserializeConverter.getJsonNodeResponse(url);
		return response.getBody();
	}

	@Override
	public String updateProduct(JsonNode product, Integer productId) {
		// TODO Auto-generated method stub
		String url = PRODUCT_SERVICE_URL + "/updateProduct/" + productId;
		HttpEntity<JsonNode> requestEntity = new HttpEntity<JsonNode>(product);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
		return response.getBody();
	}

	@Override
	public String deleteProduct(Integer productId) {
		// TODO Auto-generated method stub
		String url = PRODUCT_SERVICE_URL + "/deleteProduct/" + productId;
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);
		return response.getBody();
	}

	@Override
	public List<JsonNode> listAll() {
		// TODO Auto-generated method stub
		String url = PRODUCT_SERVICE_URL + "/allProducts";
		return deserializeConverter.getJsonNodeList(url);
	}

	@Override
	public List<JsonNode> listAllByCategoryId(Integer categoryId) {
		// TODO Auto-generated method stub
		String url = PRODUCT_SERVICE_URL + "/allProducts/" + categoryId;
		return deserializeConverter.getJsonNodeList(url);
	}

}

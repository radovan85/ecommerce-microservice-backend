package com.radovan.spring.services.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.radovan.spring.converter.DeserializeConverter;
import com.radovan.spring.services.ProductImageService;
import com.radovan.spring.utils.MultipartFileResource;

@Service
public class ProductImageServiceImpl implements ProductImageService {

	private final String PRODUCT_SERVICE_URL = "http://localhost:8084/products";

	@Autowired
	private DeserializeConverter deserializeConverter;

	@Autowired
	private RestTemplate restTemplate;

	/*
	@Override
	public String addImage(MultipartFile file, Integer productId) {
		// TODO Auto-generated method stub
		String productUrl = PRODUCT_SERVICE_URL + "/createProduct";
		HttpEntity<MultipartFile> requestEntity = new HttpEntity<MultipartFile>(file);
		ResponseEntity<String> response = restTemplate.exchange(productUrl, HttpMethod.POST, requestEntity,
				String.class);
		return response.getBody();
	}
	*/
	
	@Override
	public String addImage(MultipartFile file, Integer productId) {
	    String productUrl = PRODUCT_SERVICE_URL + "/storeImage/" + productId; // proveri URL ako treba

	    try {
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

	        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
	        body.add("file", new MultipartFileResource(file)); // koristi MultipartFileResource za ispravan format

	        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
	        ResponseEntity<String> response = restTemplate.exchange(productUrl, HttpMethod.POST, requestEntity, String.class);

	        return response.getBody();
	    } catch (IOException e) {
	        throw new RuntimeException("Failed to convert file to resource", e);
	    }
	}



	@Override
	public List<JsonNode> listAll() {
		// TODO Auto-generated method stub
		String url = PRODUCT_SERVICE_URL + "/allImages";
		return deserializeConverter.getJsonNodeList(url);
	}

}

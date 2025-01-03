package com.radovan.spring.converter;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import flexjson.JSONDeserializer;

@Component
public class DeserializeConverter {
	
	@Autowired
	private RestTemplate restTemplate;

	public Map<String, Object> deserializeJson(String jsonString) {
	    JSONDeserializer<Map<String, Object>> deserializer = new JSONDeserializer<>();
	    return deserializer.deserialize(jsonString);
	}
	
	public ResponseEntity<JsonNode> getJsonNodeResponse(String url){
		return restTemplate.getForEntity(url, JsonNode.class);
	}
}

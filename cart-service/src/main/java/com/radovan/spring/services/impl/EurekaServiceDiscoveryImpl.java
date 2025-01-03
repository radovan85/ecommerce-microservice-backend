package com.radovan.spring.services.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.radovan.spring.services.EurekaServiceDiscovery;

import flexjson.JSONDeserializer;

@Service
public class EurekaServiceDiscoveryImpl implements EurekaServiceDiscovery {

	private static final String EUREKA_SERVER_URL = "http://localhost:8761/eureka/apps";

	@Autowired
	private RestTemplate restTemplate;

	@Override
	@SuppressWarnings("unchecked")
	public String getServiceUrl(String serviceName) {
		String url = EUREKA_SERVER_URL + "/" + serviceName;
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

		String responseBody = response.getBody();

		if (responseBody == null) {
			throw new RuntimeException("Service not found: " + serviceName);
		}

		// Deserialize the JSON response directly
		Map<String, Object> app = new JSONDeserializer<Map<String, Object>>().deserialize(responseBody);
		if (app == null || !app.containsKey("application")) {
			throw new RuntimeException("Service not found: " + serviceName);
		}

		Map<String, Object> application = (Map<String, Object>) app.get("application");
		Object instanceObj = application.get("instance");

		Map<String, Object> instance;
		if (instanceObj instanceof List) {
			instance = (Map<String, Object>) ((List<?>) instanceObj).get(0);
		} else {
			instance = (Map<String, Object>) instanceObj;
		}

		return (String) instance.get("homePageUrl");
	}
}

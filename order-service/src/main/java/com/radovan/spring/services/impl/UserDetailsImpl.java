package com.radovan.spring.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.radovan.spring.utils.CustomUserDetails;

import flexjson.JSONDeserializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserDetailsImpl implements UserDetailsService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) {
        String userUrl = "http://localhost:8081/userData/" + username;

        // Dobavljanje korisničkih podataka
        JsonNode userResponse = restTemplate.getForObject(userUrl, JsonNode.class);

        // Raspakujte userResponse koristeći Flexjson
        String userJsonString = userResponse.toString();

        // Koristite Flexjson za ekstrakciju podataka
        JSONDeserializer<Map<String, Object>> deserializer = new JSONDeserializer<>();

        // Deserializacija u Map<String, Object>
        Map<String, Object> userMap = deserializer.deserialize(userJsonString);
        
        // Uzimanje vrednosti iz Map
        String id = (String) userMap.get("id");
        //String firstName = (String) userMap.get("firstName");
        //String lastName = (String) userMap.get("lastName");
        String email = (String) userMap.get("email");
        String enabled = (String) userMap.get("enabled");

        String rolesUrl = "http://localhost:8081/roles/" + id;
		ResponseEntity<JsonNode> response = restTemplate.getForEntity(rolesUrl, JsonNode.class);

		// Procesiranje response body za autoritete
		List<GrantedAuthority> authorities = new ArrayList<>();
		JsonNode rolesList = response.getBody().get("item"); // Uzimamo "item" jer znamo da je to objekat
		if (rolesList != null) {
			String roleName = rolesList.get("role").asText();
			GrantedAuthority authority = new SimpleGrantedAuthority(roleName);
			authorities.add(authority);
		} else {
			System.out.println("No roles found for this user.");
		}
		
		CustomUserDetails customUserDetails = new CustomUserDetails();
		customUserDetails.setEmail(email);
		customUserDetails.setEnabled(Byte.valueOf(enabled));
		customUserDetails.setAuthorities(authorities);

		return customUserDetails; // Vraćamo CustomUserDetails

    }
}

package com.radovan.spring.config;

import java.util.Collections;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.radovan.spring.interceptors.AuthInterceptor;
import com.radovan.spring.interceptors.RestTemplateHeaderModifierInterceptor;

@Configuration
@ComponentScan(basePackages = "com.radovan.spring")
public class SpringBootConfiguration implements WebMvcConfigurer {

	@Autowired
	private AuthInterceptor authInterceptor;

	@Bean
	public ModelMapper getMapper() {
		ModelMapper returnValue = new ModelMapper();
		returnValue.getConfiguration().setAmbiguityIgnored(true).setFieldAccessLevel(AccessLevel.PRIVATE);
		returnValue.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		return returnValue;
	}

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		// Dodaj interceptor za dinamičko dodavanje Authorization zaglavlja
		restTemplate.setInterceptors(Collections.singletonList(new RestTemplateHeaderModifierInterceptor()));
		return restTemplate;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authInterceptor);
	}

}

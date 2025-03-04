package com.radovan.spring.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.radovan.spring.interceptors.RestTemplateHeaderModifierInterceptor;

@Configuration
@EnableScheduling
@EnableWebMvc
@ComponentScan(basePackages = "com.radovan.spring")
public class SpringMvcConfiguration implements WebMvcConfigurer {

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		// Dodaj interceptor za dinamičko dodavanje Authorization zaglavlja
		restTemplate.setInterceptors(Collections.singletonList(new RestTemplateHeaderModifierInterceptor()));
		return restTemplate;
	}

}

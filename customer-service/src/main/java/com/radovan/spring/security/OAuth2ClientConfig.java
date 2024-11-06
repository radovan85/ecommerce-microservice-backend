package com.radovan.spring.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

@Configuration
public class OAuth2ClientConfig {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("customer-service")
                .clientId("customer-service")
                .clientSecret("{noop}customer-secret")
                .scope("read")
                .authorizationUri("http://user-service/oauth2/authorize")
                .tokenUri("http://user-service/oauth2/token")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE) // Dodato
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}") // Ispravljeno
                .build();

        return new InMemoryClientRegistrationRepository(clientRegistration);
    }
}

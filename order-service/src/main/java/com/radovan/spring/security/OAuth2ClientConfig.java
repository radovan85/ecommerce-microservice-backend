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
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("order-service")
                .clientId("order-service")
                .clientSecret("{noop}cart-secret")
                .scope("read")
                .authorizationUri("http://user-service/oauth2/authorize")
                .tokenUri("http://user-service/oauth2/token")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE) 
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}") 
                .build();

        return new InMemoryClientRegistrationRepository(clientRegistration);
    }
}

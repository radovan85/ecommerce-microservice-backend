package com.radovan.spring.interceptors;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.IOException;

@Component
public class RestTemplateHeaderModifierInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes != null) {
            String authHeader = (String) requestAttributes.getAttribute("Authorization", RequestAttributes.SCOPE_REQUEST);

            if (authHeader != null) {
                request.getHeaders().add("Authorization", authHeader);
            }
        }

        request.getHeaders().set("X-Source-Service", "order-service");

        return execution.execute(request, body);
    }
}


package com.radovan.spring.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.radovan.spring.services.EurekaServiceDiscovery;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ServiceUrlProvider {

    private final Map<String, String> cachedServiceUrls = new ConcurrentHashMap<>();

    @Autowired
    private EurekaServiceDiscovery eurekaServiceDiscovery;

    public String getServiceUrl(String serviceName) {
        return cachedServiceUrls.computeIfAbsent(serviceName, key -> {
            try {
                String serviceUrl = eurekaServiceDiscovery.getServiceUrl(serviceName);
                validateUrl(serviceUrl, serviceName);
                return serviceUrl;
            } catch (RuntimeException e) {
            	System.err.println("Failed to retrieve service URL for: " + serviceName + " - " + e.getMessage());
                throw e;
            }
        });
    }

    public String getCartServiceUrl() {
        return getServiceUrl("CART-SERVICE");
    }

    public String getCustomerServiceUrl() {
        return getServiceUrl("CUSTOMER-SERVICE");
    }

    public String getOrderServiceUrl() {
        return getServiceUrl("ORDER-SERVICE");
    }

    public String getProductServiceUrl() {
        return getServiceUrl("PRODUCT-SERVICE");
    }

    public String getUserServiceUrl() {
        return getServiceUrl("USER-SERVICE");
    }

    private void validateUrl(String url, String serviceName) {
        if (url == null || !url.startsWith("http")) {
            throw new IllegalArgumentException("Invalid URL for " + serviceName + ": " + url);
        }
    }
}

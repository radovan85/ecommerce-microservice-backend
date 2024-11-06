package com.radovan.spring.services;

import com.fasterxml.jackson.databind.JsonNode;

public interface ShippingAddressService {

	String updateAddress(JsonNode address);
}

package com.radovan.spring.services;

import com.radovan.spring.dto.ShippingAddressDto;

public interface ShippingAddressService {

	ShippingAddressDto addAddress(ShippingAddressDto address);
	
	ShippingAddressDto updateAddress(ShippingAddressDto address);
	
	ShippingAddressDto getAddressById(Integer addressId);
	
}

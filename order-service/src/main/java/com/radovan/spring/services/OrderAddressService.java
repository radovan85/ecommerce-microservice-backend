package com.radovan.spring.services;

import java.util.List;

import com.radovan.spring.dto.OrderAddressDto;

public interface OrderAddressService {

	List<OrderAddressDto> listAll();
}
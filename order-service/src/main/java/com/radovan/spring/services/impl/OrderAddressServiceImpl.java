package com.radovan.spring.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.OrderAddressDto;
import com.radovan.spring.entity.OrderAddressEntity;
import com.radovan.spring.repositories.OrderAddressRepository;
import com.radovan.spring.services.OrderAddressService;

@Service
public class OrderAddressServiceImpl implements OrderAddressService {

	@Autowired
	private OrderAddressRepository addressRepository;

	@Autowired
	private TempConverter tempConverter;

	@Override
	@Transactional(readOnly = true)
	public List<OrderAddressDto> listAll() {
		// TODO Auto-generated method stub
		List<OrderAddressEntity> allAddresses = addressRepository.findAll();
		return allAddresses.stream().map(tempConverter::orderAddressEntityToDto).collect(Collectors.toList());
	}

}
package com.radovan.spring.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.CustomerDto;
import com.radovan.spring.dto.ShippingAddressDto;
import com.radovan.spring.entity.ShippingAddressEntity;
import com.radovan.spring.exceptions.InstanceUndefinedException;
import com.radovan.spring.repositories.ShippingAddressRepository;
import com.radovan.spring.services.CustomerService;
import com.radovan.spring.services.ShippingAddressService;

@Service
public class ShippingAddressServiceImpl implements ShippingAddressService {

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private ShippingAddressRepository addressRepository;
	
	@Autowired
	private CustomerService customerService;

	@Override
	@Transactional
	public ShippingAddressDto addAddress(ShippingAddressDto address) {
		// TODO Auto-generated method stub
		ShippingAddressEntity storedAddress = addressRepository.save(tempConverter.addressDtoToEntity(address));
		return tempConverter.addressEntityToDto(storedAddress);
	}

	@Override
	@Transactional
	public ShippingAddressDto updateAddress(ShippingAddressDto address) {
		// TODO Auto-generated method stub
		CustomerDto currentCustomer = customerService.getCurrentCustomer();
		ShippingAddressDto currentAddress = getAddressById(currentCustomer.getShippingAddressId());
		address.setShippingAddressId(currentAddress.getShippingAddressId());
		address.setCustomerId(currentAddress.getCustomerId());
		ShippingAddressEntity updatedAddress = addressRepository
				.saveAndFlush(tempConverter.addressDtoToEntity(address));
		return tempConverter.addressEntityToDto(updatedAddress);
	}

	@Override
	@Transactional(readOnly = true)
	public ShippingAddressDto getAddressById(Integer addressId) {
		// TODO Auto-generated method stub
		ShippingAddressEntity addressEntity = addressRepository.findById(addressId).orElseThrow(
				() -> new InstanceUndefinedException(new Error("The shipping address has not ben found!")));
		return tempConverter.addressEntityToDto(addressEntity);
	}

}

package com.radovan.spring.services;

import java.util.List;

import com.radovan.spring.dto.CustomerDto;
import com.radovan.spring.utils.RegistrationForm;

public interface CustomerService {

	CustomerDto addCustomer(RegistrationForm form);

	List<CustomerDto> listAll();

	CustomerDto getCustomerById(Integer customerId);

	CustomerDto getCustomerByUserId(Integer userId);

	void removeCustomer(Integer customerId);

	CustomerDto getCurrentCustomer();

	void updateCustomerCartId(Integer customerId, Integer cartId);

	void suspendCustomer(Integer customerId);

	void reactivateCustomer(Integer customerId);
}

package com.radovan.spring.services.impl;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.CustomerDto;
import com.radovan.spring.dto.ShippingAddressDto;
import com.radovan.spring.entity.CustomerEntity;
import com.radovan.spring.exceptions.InstanceUndefinedException;
import com.radovan.spring.repositories.CustomerRepository;
import com.radovan.spring.services.CustomerService;
import com.radovan.spring.services.ShippingAddressService;
import com.radovan.spring.utils.RegistrationForm;

import flexjson.JSONDeserializer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ShippingAddressService addressService;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private TempConverter tempConverter;

	private final String ORDER_SERVICE_URL = "http://localhost:8085/order/";

	private final String CART_SERVICE_URL = "http://localhost:8083/cart/";

	private final String USER_SERVICE_URL = "http://localhost:8081";

	@Override
	@Transactional
	public CustomerDto addCustomer(RegistrationForm form) {
		// Dodavanje adrese
		ShippingAddressDto storedAddress = addressService.addAddress(form.getAddress());

		CustomerDto customer = form.getCustomer();
		customer.setShippingAddressId(storedAddress.getShippingAddressId());

		// Kreiranje korisnika
		JsonNode user = form.getUser();
		HttpEntity<JsonNode> userRequestEntity = new HttpEntity<>(user);

		String userServiceUrl = "http://localhost:8081/addUser";
		ResponseEntity<JsonNode> response = restTemplate.exchange(userServiceUrl, HttpMethod.POST, userRequestEntity,
				JsonNode.class);

		// Uzimanje cartId iz CartIdHolder
		String cartServiceUrl = "http://localhost:8083/cart/addCart";
		// Pozovite addCart endpoint sa header-ima
		ResponseEntity<JsonNode> cartResponse = restTemplate.exchange(cartServiceUrl, HttpMethod.POST, null,
				JsonNode.class);

		if (cartResponse.getStatusCode().is2xxSuccessful() && cartResponse.getBody() != null) {
			Integer cartId = cartResponse.getBody().get("cartId").asInt();
			customer.setCartId(cartId);
		} else {
			// Logika za neuspešan odgovor
			throw new InstanceUndefinedException(new Error("Cart id not found for customer"));
		}

		if (response.getStatusCode().is2xxSuccessful()) {
			JsonNode responseBody = response.getBody();
			Integer userId = new JSONDeserializer<Integer>().deserialize(responseBody.get("id").toString(),
					Integer.class);
			customer.setUserId(userId);
			customer.setShippingAddressId(storedAddress.getShippingAddressId());

			// Čuvanje kupca u bazi
			CustomerEntity storedCustomer = customerRepository.save(tempConverter.customerDtoToEntity(customer));
			storedAddress.setCustomerId(storedCustomer.getCustomerId());
			addressService.addAddress(storedAddress);

			return tempConverter.customerEntityToDto(storedCustomer);
		}

		return null; // Ili neka druga logika za neuspeh
	}

	@Override
	@Transactional(readOnly = true)
	public List<CustomerDto> listAll() {
		// TODO Auto-generated method stub
		List<CustomerEntity> allCustomers = customerRepository.findAll();
		return allCustomers.stream().map(tempConverter::customerEntityToDto).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public CustomerDto getCustomerById(Integer customerId) {
		// TODO Auto-generated method stub
		CustomerEntity customerEntity = customerRepository.findById(customerId)
				.orElseThrow(() -> new InstanceUndefinedException(new Error("The customer has not been found")));
		return tempConverter.customerEntityToDto(customerEntity);
	}

	@Override
	@Transactional(readOnly = true)
	public CustomerDto getCustomerByUserId(Integer userId) {
		// TODO Auto-generated method stub
		CustomerEntity customerEntity = customerRepository.findByUserId(userId)
				.orElseThrow(() -> new InstanceUndefinedException(new Error("The customer has not been found")));
		return tempConverter.customerEntityToDto(customerEntity);
	}

	@Override
	@Transactional
	public void removeCustomer(Integer customerId) {
		// TODO Auto-generated method stub
		CustomerDto customer = getCustomerById(customerId);
		customerRepository.deleteById(customerId);
		customerRepository.flush();
		String userServiceUrl = "http://localhost:8081/deleteUser/" + customer.getUserId();
		restTemplate.exchange(userServiceUrl, HttpMethod.DELETE, null, String.class);
		String deleteOrdersUrl = ORDER_SERVICE_URL + "deleteAllByCartId/" + customer.getCartId();
		restTemplate.exchange(deleteOrdersUrl, HttpMethod.DELETE, null, Void.class);
		String deleteCartUrl = CART_SERVICE_URL + "deleteCart/" + customer.getCartId();
		restTemplate.exchange(deleteCartUrl, HttpMethod.DELETE, null, Void.class);
	}

	@Override
	@Transactional(readOnly = true)
	public CustomerDto getCurrentCustomer() {
		// TODO Auto-generated method stub
		String userUrl = "http://localhost:8081/currentUser";
		JsonNode userResponse = restTemplate.getForObject(userUrl, JsonNode.class);
		String userJsonString = userResponse.toString();
		Map<String, Object> userMap = tempConverter.deserializeJson(userJsonString);
		String userId = (String) userMap.get("id");
		return getCustomerByUserId(Integer.valueOf(userId));
	}

	@Override
	@Transactional
	public void updateCustomerCartId(Integer customerId, Integer cartId) {
		CustomerEntity customerEntity = customerRepository.findById(customerId)
				.orElseThrow(() -> new InstanceUndefinedException(new Error("The customer has not been found")));

		customerEntity.setCartId(cartId);
		customerRepository.save(customerEntity);
	}

	@Override
	@Transactional
	public void suspendCustomer(Integer customerId) {
		// TODO Auto-generated method stub
		CustomerDto customer = getCustomerById(customerId);
		String url = USER_SERVICE_URL + "/suspendUser/" + customer.getUserId();
		restTemplate.exchange(url, HttpMethod.PUT, null, Void.class);
	}

	@Override
	@Transactional
	public void reactivateCustomer(Integer customerId) {
		// TODO Auto-generated method stub
		CustomerDto customer = getCustomerById(customerId);
		String url = USER_SERVICE_URL + "/reactivateUser/" + customer.getUserId();
		restTemplate.exchange(url, HttpMethod.PUT, null, Void.class);
	}

}

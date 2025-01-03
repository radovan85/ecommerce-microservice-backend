package com.radovan.spring.services.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.radovan.spring.converter.DeserializeConverter;
import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.OrderAddressDto;
import com.radovan.spring.dto.OrderDto;
import com.radovan.spring.dto.OrderItemDto;

import com.radovan.spring.entity.OrderAddressEntity;
import com.radovan.spring.entity.OrderEntity;
import com.radovan.spring.entity.OrderItemEntity;
import com.radovan.spring.exceptions.InstanceUndefinedException;
import com.radovan.spring.exceptions.OutOfStockException;
import com.radovan.spring.repositories.OrderAddressRepository;
import com.radovan.spring.repositories.OrderItemRepository;
import com.radovan.spring.repositories.OrderRepository;
import com.radovan.spring.services.OrderService;
import com.radovan.spring.utils.ServiceUrlProvider;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderAddressRepository orderAddressRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private DeserializeConverter deserializeConverter;
	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private RestTemplate restTemplate;

	private ZoneId zoneId = ZoneId.of("UTC");

	@Autowired
	private ServiceUrlProvider urlProvider;

	@Override
	@Transactional
	public OrderDto addOrder() {
		Map<String, Object> customerMap = new HashMap<>();
		Map<String, Object> cartMap = new HashMap<>();
		Map<String, Object> shippingAddressMap = new HashMap<>();
		ResponseEntity<JsonNode> customerResponse = deserializeConverter
				.getJsonNodeResponse(urlProvider.getCustomerServiceUrl() + "/customers/currentCustomer");
		if (customerResponse.getStatusCode().is2xxSuccessful()) {
			customerMap = deserializeConverter.deserializeJson(customerResponse.getBody().toString());
		} else {
			throw new InstanceUndefinedException(new Error("The customer response has not been found!"));
		}

		Integer cartId = Integer.valueOf(customerMap.get("cartId").toString());
		Integer shippingAddressId = Integer.valueOf(customerMap.get("shippingAddressId").toString());

		ResponseEntity<JsonNode> cartResponse = deserializeConverter
				.getJsonNodeResponse(urlProvider.getCartServiceUrl() + "/cart/validateCart/" + cartId);
		if (cartResponse.getStatusCode().is2xxSuccessful()) {
			cartMap = deserializeConverter.deserializeJson(cartResponse.getBody().toString());
		}
		Float cartPrice = Float.valueOf(cartMap.get("cartPrice").toString());

		OrderDto orderDto = new OrderDto();
		orderDto.setCartId(cartId);
		orderDto.setOrderPrice(cartPrice);

		ResponseEntity<JsonNode> shippingAddressResponse = deserializeConverter.getJsonNodeResponse(
				urlProvider.getCustomerServiceUrl() + "/addresses/addressDetails/" + shippingAddressId);
		if (shippingAddressResponse.getStatusCode().is2xxSuccessful()) {
			shippingAddressMap = deserializeConverter.deserializeJson(shippingAddressResponse.getBody().toString());
		}

		OrderAddressDto orderAddress = new OrderAddressDto();
		orderAddress.setAddress(shippingAddressMap.get("address").toString());
		orderAddress.setCity(shippingAddressMap.get("city").toString());
		orderAddress.setCountry(shippingAddressMap.get("country").toString());
		orderAddress.setState(shippingAddressMap.get("state").toString());
		orderAddress.setPostcode(shippingAddressMap.get("postcode").toString());

		OrderAddressEntity storedAddress = orderAddressRepository
				.save(tempConverter.orderAddressDtoToEntity(orderAddress));

		ZonedDateTime currentTime = Instant.now().atZone(zoneId);
		Timestamp currentTimeStamp = Timestamp.valueOf(currentTime.toLocalDateTime());

		OrderEntity orderEntity = tempConverter.orderDtoToEntity(orderDto);
		orderEntity.setAddress(storedAddress);
		orderEntity.setCreatedAt(currentTimeStamp);
		OrderEntity storedOrder = orderRepository.save(orderEntity);

		List<OrderItemEntity> orderedItems = new ArrayList<>();
		List<JsonNode> cartItems = deserializeConverter
				.getJsonNodeList(urlProvider.getCartServiceUrl() + "/items/allItemsByCartId/" + cartId);
		for (JsonNode cartItem : cartItems) {
			Map<String, Object> itemMap = deserializeConverter.deserializeJson(cartItem.toString());
			Integer quantity = Integer.valueOf(itemMap.get("quantity").toString());
			Integer productId = Integer.valueOf(itemMap.get("productId").toString());
			ResponseEntity<JsonNode> productResponse = deserializeConverter
					.getJsonNodeResponse(urlProvider.getProductServiceUrl() + "/products/productDetails/" + productId);
			Map<String, Object> productMap = deserializeConverter.deserializeJson(productResponse.getBody().toString());
			Integer unitStock = Integer.valueOf(productMap.get("unitStock").toString());
			String productName = productMap.get("productName").toString();
			if (quantity > unitStock) {
				throw new OutOfStockException(new Error("There is a shortage of " + productName + " in stock"));
			} else {
				unitStock = unitStock - quantity;
				productMap.put("unitStock", unitStock);
				ObjectMapper mapper = new ObjectMapper();
				JsonNode product = mapper.valueToTree(productMap);
				String productUrl = urlProvider.getProductServiceUrl() + "/products/orderUpdateProduct/" + productId;
				HttpEntity<JsonNode> requestEntity = new HttpEntity<JsonNode>(product);
				restTemplate.exchange(productUrl, HttpMethod.PUT, requestEntity, Void.class);
			}
			OrderItemDto orderItemDto = new OrderItemDto();
			orderItemDto.setOrderId(storedOrder.getOrderId());
			orderItemDto.setPrice(Float.valueOf(itemMap.get("price").toString()));
			orderItemDto.setProductDiscount(Float.valueOf(productMap.get("discount").toString()));
			orderItemDto.setProductName(productName);
			orderItemDto.setProductPrice(Float.valueOf(productMap.get("productPrice").toString()));
			orderItemDto.setQuantity(quantity);
			OrderItemEntity orderItemEntity = tempConverter.orderItemDtoToEntity(orderItemDto);
			orderedItems.add(orderItemRepository.save(orderItemEntity));
		}

		storedOrder.getOrderedItems().addAll(orderedItems);
		storedOrder = orderRepository.saveAndFlush(storedOrder);

		String clearCartUrl = urlProvider.getCartServiceUrl() + "/cart/clearCart";
		HttpEntity<String> clearCartRequestEntity = new HttpEntity<>(clearCartUrl);
		restTemplate.exchange(clearCartUrl, HttpMethod.DELETE, clearCartRequestEntity, String.class);
		return tempConverter.orderEntityToDto(storedOrder);
	}

	@Override
	@Transactional(readOnly = true)
	public OrderDto getOrderById(Integer orderId) {
		OrderEntity orderEntity = orderRepository.findById(orderId)
				.orElseThrow(() -> new InstanceUndefinedException(new Error("The order has not been found!")));
		return tempConverter.orderEntityToDto(orderEntity);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderDto> listAll() {
		List<OrderEntity> allOrders = orderRepository.findAll();
		return allOrders.stream().map(tempConverter::orderEntityToDto).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderDto> listAllByCartId(Integer cartId) {
		List<OrderEntity> allOrders = orderRepository.findAllByCartId(cartId);
		return allOrders.stream().map(tempConverter::orderEntityToDto).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void deleteOrder(Integer orderId) {
		getOrderById(orderId);
		orderRepository.deleteById(orderId);
		orderRepository.flush();
	}

	@Override
	@Transactional
	public void deleteAllByCartId(Integer cartId) {
		// TODO Auto-generated method stub
		List<OrderDto> allOrders = listAllByCartId(cartId);
		allOrders.forEach((order) -> {
			deleteOrder(order.getOrderId());
		});
	}

}

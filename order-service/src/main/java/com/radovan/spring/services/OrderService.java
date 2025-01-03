package com.radovan.spring.services;

import java.util.List;

import com.radovan.spring.dto.OrderDto;

public interface OrderService {

	OrderDto addOrder();

	OrderDto getOrderById(Integer orderId);

	List<OrderDto> listAll();

	List<OrderDto> listAllByCartId(Integer cartId);

	void deleteOrder(Integer orderId);
	
	void deleteAllByCartId(Integer cartId);
}

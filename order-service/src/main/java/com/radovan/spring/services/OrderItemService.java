package com.radovan.spring.services;

import java.util.List;

import com.radovan.spring.dto.OrderItemDto;

public interface OrderItemService {

	List<OrderItemDto> listAllByOrderId(Integer orderId);
}
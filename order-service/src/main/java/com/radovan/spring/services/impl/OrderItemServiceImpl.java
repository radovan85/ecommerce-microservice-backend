package com.radovan.spring.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.OrderItemDto;
import com.radovan.spring.entity.OrderItemEntity;
import com.radovan.spring.repositories.OrderItemRepository;
import com.radovan.spring.services.OrderItemService;
import com.radovan.spring.services.OrderService;

@Service
public class OrderItemServiceImpl implements OrderItemService {

	@Autowired
	private OrderItemRepository itemRepository;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private OrderService orderService;

	@Override
	@Transactional(readOnly = true)
	public List<OrderItemDto> listAllByOrderId(Integer orderId) {
		// TODO Auto-generated method stub
		orderService.getOrderById(orderId);
		List<OrderItemEntity> allItems = itemRepository.listAllByOrderId(orderId);
		return allItems.stream().map(tempConverter::orderItemEntityToDto).collect(Collectors.toList());
	}

}

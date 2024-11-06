package com.radovan.spring.converter;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.radovan.spring.dto.OrderAddressDto;
import com.radovan.spring.dto.OrderDto;
import com.radovan.spring.dto.OrderItemDto;
import com.radovan.spring.entity.OrderAddressEntity;
import com.radovan.spring.entity.OrderEntity;
import com.radovan.spring.entity.OrderItemEntity;
import com.radovan.spring.repositories.OrderItemRepository;
import com.radovan.spring.repositories.OrderRepository;

@Component
public class TempConverter {

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private final ZoneId zoneId = ZoneId.of("UTC");

	public OrderAddressDto orderAddressEntityToDto(OrderAddressEntity address) {
		// TODO Auto-generated method stub
		OrderAddressDto returnValue = mapper.map(address, OrderAddressDto.class);
		Optional<OrderEntity> orderOptional = Optional.ofNullable(address.getOrder());
		if (orderOptional.isPresent()) {
			returnValue.setOrderId(orderOptional.get().getOrderId());
		}
		return returnValue;
	}

	public OrderAddressEntity orderAddressDtoToEntity(OrderAddressDto address) {
		// TODO Auto-generated method stub
		OrderAddressEntity returnValue = mapper.map(address, OrderAddressEntity.class);
		Optional<Integer> orderIdOptional = Optional.ofNullable(address.getOrderId());
		if (orderIdOptional.isPresent()) {
			Integer orderId = orderIdOptional.get();
			OrderEntity orderEntity = orderRepository.findById(orderId).orElse(null);
			if (orderEntity != null) {
				returnValue.setOrder(orderEntity);
			}
		}
		return returnValue;
	}

	public OrderItemDto orderItemEntityToDto(OrderItemEntity orderItem) {
		// TODO Auto-generated method stub
		OrderItemDto returnValue = mapper.map(orderItem, OrderItemDto.class);
		Optional<OrderEntity> orderoOptional = Optional.ofNullable(orderItem.getOrder());
		if (orderoOptional.isPresent()) {
			returnValue.setOrderId(orderoOptional.get().getOrderId());
		}
		return returnValue;
	}

	public OrderItemEntity orderItemDtoToEntity(OrderItemDto orderItem) {
		// TODO Auto-generated method stub
		OrderItemEntity returnValue = mapper.map(orderItem, OrderItemEntity.class);
		Optional<Integer> orderIdOptional = Optional.ofNullable(orderItem.getOrderId());
		if (orderIdOptional.isPresent()) {
			Integer orderId = orderIdOptional.get();
			OrderEntity orderEntity = orderRepository.findById(orderId).orElse(null);
			if (orderEntity != null) {
				returnValue.setOrder(orderEntity);
			}
		}
		return returnValue;
	}

	public OrderDto orderEntityToDto(OrderEntity order) {
		// TODO Auto-generated method stub
		OrderDto returnValue = mapper.map(order, OrderDto.class);
		List<Integer> orderItemsIds = new ArrayList<>();
		Optional<List<OrderItemEntity>> itemsOptional = Optional.ofNullable(order.getOrderedItems());
		if (!itemsOptional.isEmpty()) {
			itemsOptional.get().forEach((item) -> {
				orderItemsIds.add(item.getOrderItemId());
			});
		}

		returnValue.setOrderedItemsIds(orderItemsIds);

		Optional<OrderAddressEntity> addressOptional = Optional.ofNullable(order.getAddress());
		if (addressOptional.isPresent()) {
			returnValue.setAddressId(addressOptional.get().getOrderAddressId());
		}

		Optional<Timestamp> createdAtOptional = Optional.ofNullable(order.getCreatedAt());
		if (createdAtOptional.isPresent()) {
			ZonedDateTime createdAtZoned = createdAtOptional.get().toLocalDateTime().atZone(zoneId);
			String createdAtStr = createdAtZoned.format(formatter);
			returnValue.setCreatedAt(createdAtStr);
		}

		return returnValue;
	}

	public OrderEntity orderDtoToEntity(OrderDto order) {
		// TODO Auto-generated method stub
		OrderEntity returnValue = mapper.map(order, OrderEntity.class);
		Optional<List<Integer>> itemsIdsOptional = Optional.ofNullable(order.getOrderedItemsIds());
		List<OrderItemEntity> orderedItems = new ArrayList<>();
		if (!itemsIdsOptional.isEmpty()) {
			itemsIdsOptional.get().forEach((itemId) -> {
				OrderItemEntity itemEntity = orderItemRepository.findById(itemId).orElse(null);
				if (itemEntity != null) {
					orderedItems.add(itemEntity);
				}
			});
		}

		returnValue.setOrderedItems(orderedItems);

		return returnValue;
	}
}

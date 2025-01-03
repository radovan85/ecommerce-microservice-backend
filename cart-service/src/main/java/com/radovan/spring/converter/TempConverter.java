package com.radovan.spring.converter;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.radovan.spring.dto.CartDto;
import com.radovan.spring.dto.CartItemDto;
import com.radovan.spring.entity.CartEntity;
import com.radovan.spring.entity.CartItemEntity;
import com.radovan.spring.repositories.CartItemRepository;
import com.radovan.spring.repositories.CartRepository;
import com.radovan.spring.utils.ServiceUrlProvider;

@Component
public class TempConverter {

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private CartItemRepository itemRepository;

	@Autowired
	private CartRepository cartRepository;

	private final DecimalFormat decfor = new DecimalFormat("#.##");

	@Autowired
	private DeserializeConverter deserializeConverter;

	@Autowired
	private ServiceUrlProvider urlProvider;

	public CartDto cartEntityToDto(CartEntity cartEntity) {
		CartDto returnValue = mapper.map(cartEntity, CartDto.class);
		List<Integer> cartItemsIds = Optional.ofNullable(cartEntity.getCartItems()).stream().flatMap(List::stream)
				.map(CartItemEntity::getCartItemId).collect(Collectors.toList());

		returnValue.setCartItemsIds(cartItemsIds);
		returnValue.setCartPrice(Float.valueOf(decfor.format(returnValue.getCartPrice())));

		return returnValue;
	}

	public CartEntity cartDtoToEntity(CartDto cartDto) {
		CartEntity returnValue = mapper.map(cartDto, CartEntity.class);
		List<CartItemEntity> cartItems = Optional.ofNullable(cartDto.getCartItemsIds()).stream().flatMap(List::stream)
				.map(itemRepository::findById).filter(Optional::isPresent).map(Optional::get)
				.collect(Collectors.toList());

		returnValue.setCartItems(cartItems);
		returnValue.setCartPrice(Float.valueOf(decfor.format(returnValue.getCartPrice())));

		return returnValue;
	}

	public CartItemDto cartItemEntityToDto(CartItemEntity cartItem) {
		// TODO Auto-generated method stub
		CartItemDto returnValue = mapper.map(cartItem, CartItemDto.class);
		Optional<ResponseEntity<JsonNode>> productResponseOptional = Optional
				.ofNullable(deserializeConverter.getJsonNodeResponse(
						urlProvider.getProductServiceUrl() + "/products/productDetails/" + cartItem.getProductId()));
		if (productResponseOptional.isPresent()) {
			Map<String, Object> productMap = deserializeConverter
					.deserializeJson(productResponseOptional.get().getBody().toString());

			Float discount = Float.valueOf((String) productMap.get("discount"));
			Float productPrice = Float.valueOf((String) productMap.get("productPrice"));
			Float itemPrice = productPrice - ((productPrice * discount) / 100);
			itemPrice = itemPrice * cartItem.getQuantity();
			returnValue.setPrice(Float.valueOf(decfor.format(itemPrice)));

			returnValue.setProductId(Integer.valueOf((String) productMap.get("productId")));

		}

		Optional<CartEntity> cartOptional = Optional.ofNullable(cartItem.getCart());
		if (cartOptional.isPresent()) {
			returnValue.setCartId(cartOptional.get().getCartId());
		}

		return returnValue;
	}

	public CartItemEntity cartItemDtoToEntity(CartItemDto cartItem) {
		// TODO Auto-generated method stub
		CartItemEntity returnValue = mapper.map(cartItem, CartItemEntity.class);
		Optional<ResponseEntity<JsonNode>> productResponseOptional = Optional
				.ofNullable(deserializeConverter.getJsonNodeResponse(
						urlProvider.getProductServiceUrl() + "/products/productDetails/" + cartItem.getProductId()));
		if (productResponseOptional.isPresent()) {
			Map<String, Object> productMap = deserializeConverter
					.deserializeJson(productResponseOptional.get().getBody().toString());
			Integer productId = Integer.valueOf((String) productMap.get("productId")).intValue();

			Float discount = Float.valueOf((String) productMap.get("discount"));
			Float productPrice = Float.valueOf((String) productMap.get("productPrice"));
			Float itemPrice = productPrice - ((productPrice * discount) / 100);
			itemPrice = itemPrice * cartItem.getQuantity();
			returnValue.setPrice(Float.valueOf(decfor.format(itemPrice)));

			returnValue.setProductId(productId);

		}

		Optional<Integer> cartIdOptional = Optional.ofNullable(cartItem.getCartId());
		if (cartIdOptional.isPresent()) {
			Integer cartId = cartIdOptional.get();
			CartEntity cartEntity = cartRepository.findById(cartId).orElse(null);
			if (cartEntity != null) {
				returnValue.setCart(cartEntity);
			}
		}

		return returnValue;
	}

}

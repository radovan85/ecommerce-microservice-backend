package com.radovan.spring.services.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.radovan.spring.converter.DeserializeConverter;
import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.CartItemDto;
import com.radovan.spring.entity.CartItemEntity;
import com.radovan.spring.exceptions.InstanceUndefinedException;
import com.radovan.spring.exceptions.OperationNotAllowedException;
import com.radovan.spring.exceptions.OutOfStockException;
import com.radovan.spring.repositories.CartItemRepository;
import com.radovan.spring.services.CartItemService;
import com.radovan.spring.services.CartService;
import com.radovan.spring.utils.ServiceUrlProvider;

@Service
public class CartItemServiceImpl implements CartItemService {

	@Autowired
	private CartService cartService;

	@Autowired
	private CartItemRepository itemRepository;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private DeserializeConverter deserializeConverter;

	@Autowired
	private ServiceUrlProvider urlProvider;

	@Override
	@Transactional
	public CartItemDto addCartItem(Integer productId) {
		String customerUrl = urlProvider.getCustomerServiceUrl() + "/customers/currentCustomer";
		ResponseEntity<JsonNode> customerResponse = deserializeConverter.getJsonNodeResponse(customerUrl);
		Map<String, Object> customerMap = deserializeConverter.deserializeJson(customerResponse.getBody().toString());
		String cartIdStr = (String) customerMap.get("cartId");
		Integer cartId = Integer.valueOf(cartIdStr).intValue();

		String productUrl = urlProvider.getProductServiceUrl() + "/products/productDetails/" + productId;
		ResponseEntity<JsonNode> productResponse = deserializeConverter.getJsonNodeResponse(productUrl);
		Map<String, Object> productMap = deserializeConverter.deserializeJson(productResponse.getBody().toString());
		String unitStockStr = (String) productMap.get("unitStock");
		Integer unitStock = Integer.valueOf(unitStockStr).intValue();
		String productName = (String) productMap.get("productName");

		Optional<CartItemDto> existingItem = listAllByCartId(cartId).stream()
				.filter(item -> item.getProductId().equals(productId)).findFirst();

		CartItemDto cartItem;
		if (existingItem.isPresent()) {
			cartItem = existingItem.get();
			cartItem.setQuantity(cartItem.getQuantity() + 1);
		} else {
			cartItem = new CartItemDto();
			cartItem.setProductId(productId);
			cartItem.setCartId(cartId);
			cartItem.setQuantity(1);
		}

		if (unitStock < cartItem.getQuantity()) {
			throw new OutOfStockException(new Error("There is a shortage of " + productName + " in stock!"));
		}

		cartItem.setCartId(cartId);
		CartItemEntity cartItemEntity = tempConverter.cartItemDtoToEntity(cartItem);
		CartItemEntity storedItem = itemRepository.save(cartItemEntity);
		CartItemDto returnValue = tempConverter.cartItemEntityToDto(storedItem);
		cartService.refreshCartState(cartId);
		return returnValue;
	}

	@Override
	@Transactional
	public void removeCartItem(Integer itemId) {
		String customerUrl = urlProvider.getCustomerServiceUrl() + "/customers/currentCustomer";
		ResponseEntity<JsonNode> customerResponse = deserializeConverter.getJsonNodeResponse(customerUrl);
		Map<String, Object> customerMap = deserializeConverter.deserializeJson(customerResponse.getBody().toString());
		CartItemDto cartItem = getItemById(itemId);
		Integer cartId = Integer.valueOf(customerMap.get("cartId").toString());

		if (cartId != cartItem.getCartId()) {
			throw new OperationNotAllowedException(new Error("Operation not allowed!"));
		}

		itemRepository.deleteById(itemId);
		cartService.refreshCartState(cartId);
	}

	@Override
	@Transactional
	public void removeAllByCartId(Integer cartId) {
		itemRepository.deleteAllByCartId(cartId);
		cartService.refreshCartState(cartId);
	}

	@Override
	@Transactional
	public void removeAllByProductId(Integer productId) {
		// TODO Auto-generated method stub
		itemRepository.deleteAllByProductId(productId);
		itemRepository.flush();
		cartService.refreshAllCarts();
	}

	@Override
	@Transactional(readOnly = true)
	public List<CartItemDto> listAllByCartId(Integer cartId) {
		List<CartItemEntity> allItems = itemRepository.findAllByCartId(cartId);
		return allItems.stream().map(tempConverter::cartItemEntityToDto).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<CartItemDto> listAllByProductId(Integer productId) {
		// TODO Auto-generated method stub
		List<CartItemEntity> allItems = itemRepository.findAllByProductId(productId);
		return allItems.stream().map(tempConverter::cartItemEntityToDto).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public CartItemDto getItemById(Integer itemId) {
		CartItemEntity itemEntity = itemRepository.findById(itemId)
				.orElseThrow(() -> new InstanceUndefinedException(new Error("The item has not been found")));
		return tempConverter.cartItemEntityToDto(itemEntity);
	}

	@Override
	@Transactional
	public CartItemDto updateItem(Integer itemId, JsonNode item) {
		// TODO Auto-generated method stub
		CartItemDto existingItem = getItemById(itemId);
		CartItemEntity existingItemEntity = tempConverter.cartItemDtoToEntity(existingItem);
		Map<String, Object> itemMap = deserializeConverter.deserializeJson(item.toString());
		existingItemEntity.setPrice(Float.valueOf(itemMap.get("price").toString()));
		CartItemEntity updatedItem = itemRepository.saveAndFlush(existingItemEntity);
		cartService.refreshCartState(updatedItem.getCart().getCartId());
		return tempConverter.cartItemEntityToDto(updatedItem);
	}

}

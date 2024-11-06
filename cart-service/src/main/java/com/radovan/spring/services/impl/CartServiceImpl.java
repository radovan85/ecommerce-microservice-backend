package com.radovan.spring.services.impl;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.radovan.spring.converter.DeserializeConverter;
import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.CartDto;
import com.radovan.spring.dto.CartItemDto;
import com.radovan.spring.entity.CartEntity;
import com.radovan.spring.exceptions.InstanceUndefinedException;
import com.radovan.spring.exceptions.InvalidCartException;
import com.radovan.spring.repositories.CartRepository;
import com.radovan.spring.services.CartItemService;
import com.radovan.spring.services.CartService;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CartItemService cartItemService;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private DeserializeConverter deserializeConverter;

	private final String CUSTOMER_SERVICE_URL = "http://localhost:8082/customers";

	private final String PRODUCT_SERVICE_URL = "http://localhost:8084/products";

	private DecimalFormat decfor = new DecimalFormat("#.##");

	@Override
	@Transactional(readOnly = true)
	public CartDto getCartById(Integer cartId) {
		CartEntity cartEntity = cartRepository.findById(cartId)
				.orElseThrow(() -> new InstanceUndefinedException(new Error("The cart has not been found")));

		return tempConverter.cartEntityToDto(cartEntity);
	}

	@Override
	@Transactional(readOnly = true)
	public CartDto validateCart(Integer cartId) {
		CartDto cart = getCartById(cartId);
		if (cart.getCartItemsIds().isEmpty()) {
			throw new InvalidCartException(new Error("Your cart is currently empty!"));
		}
		return cart;
	}

	@Override
	@Transactional
	public void refreshCartState(Integer cartId) {
		CartDto cart = getCartById(cartId);
		Float cartPrice = cartRepository.calculateCartPrice(cartId).orElse(0f);
		cart.setCartPrice(cartPrice);
		cartRepository.saveAndFlush(tempConverter.cartDtoToEntity(cart));
	}

	@Override
	@Transactional
	public void refreshAllCarts() {
		List<CartEntity> allCarts = cartRepository.findAll();
		allCarts.forEach(cartEntity -> refreshCartState(cartEntity.getCartId()));
	}

	@Override
	@Transactional
	public CartDto addCart() {
		CartDto cart = new CartDto();
		cart.setCartPrice(0f);

		CartEntity storedCart = cartRepository.save(tempConverter.cartDtoToEntity(cart));
		CartDto returnValue = tempConverter.cartEntityToDto(storedCart);

		return returnValue;
	}

	@Override
	@Transactional(readOnly = true)
	public CartDto getMyCart() {
		// TODO Auto-generated method stub
		String url = CUSTOMER_SERVICE_URL + "/currentCustomer";
		ResponseEntity<JsonNode> response = deserializeConverter.getJsonNodeResponse(url);
		Integer cartId = response.getBody().get("cartId").asInt();
		return getCartById(cartId);
	}

	@Override
	@Transactional(readOnly = true)
	public Float calculateGrandTotal(Integer cartId) {
		Float grandTotal = 0f;
		List<CartItemDto> allCartItems = cartItemService.listAllByCartId(cartId);
		for (CartItemDto item : allCartItems) {
			// ProductDto product = productService.getProductById(item.getProductId());
			String productUrl = PRODUCT_SERVICE_URL + "/" + item.getProductId();
			ResponseEntity<JsonNode> productResponse = deserializeConverter.getJsonNodeResponse(productUrl);
			Map<String, Object> productMap = deserializeConverter.deserializeJson(productResponse.getBody().toString());
			Float productPrice = Float.valueOf(productMap.get("productPrice").toString());
			grandTotal += (productPrice * item.getQuantity());
		}
		return Float.valueOf(decfor.format(grandTotal));
	}

	@Override
	@Transactional
	public void clearCart() {
		// TODO Auto-generated method stub
		String customerUrl = CUSTOMER_SERVICE_URL + "/currentCustomer";
		ResponseEntity<JsonNode> customerResponse = deserializeConverter.getJsonNodeResponse(customerUrl);
		Map<String, Object> customerMap = deserializeConverter.deserializeJson(customerResponse.getBody().toString());
		Integer cartId = Integer.valueOf(customerMap.get("cartId").toString());
		cartItemService.removeAllByCartId(cartId);
		refreshCartState(cartId);
	}

	@Override
	@Transactional
	public void removeCart(Integer cartId) {
		// TODO Auto-generated method stub
		getCartById(cartId);
		cartRepository.deleteById(cartId);
		cartRepository.flush();
	}

}

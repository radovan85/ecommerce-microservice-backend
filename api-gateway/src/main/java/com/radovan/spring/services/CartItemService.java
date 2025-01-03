package com.radovan.spring.services;

public interface CartItemService {

	String addCartItem(Integer productId);

	String deleteItem(Integer itemId);
}

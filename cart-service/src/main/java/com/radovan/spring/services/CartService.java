package com.radovan.spring.services;

import com.radovan.spring.dto.CartDto;

public interface CartService {

	CartDto getCartById(Integer cartId);

	CartDto validateCart(Integer cartId);

	CartDto getMyCart();

	Float calculateGrandTotal(Integer cartId);

	void refreshCartState(Integer cartId);

	void refreshAllCarts();

	CartDto addCart();
	
	void clearCart();
	
	void removeCart(Integer cartId);
}

package com.radovan.spring.services;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.radovan.spring.dto.ProductDto;

public interface ProductService {

	ProductDto addProduct(ProductDto product);

	ProductDto getProductById(Integer productId);

	ProductDto updateProduct(ProductDto product, Integer productId);
	
	ProductDto updateProduct(JsonNode product, Integer productId);

	void deleteProduct(Integer productId);

	List<ProductDto> listAll();

	List<ProductDto> listAllByCategoryId(Integer categoryId);

	void deleteProductsByCategoryId(Integer categoryId);
}

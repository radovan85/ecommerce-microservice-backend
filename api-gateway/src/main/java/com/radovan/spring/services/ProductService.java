package com.radovan.spring.services;

import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;

public interface ProductService {

	String createProduct(JsonNode product);

	JsonNode getBroductById(Integer productId);

	String updateProduct(JsonNode product, Integer productId);

	String deleteProduct(Integer productId);

	List<JsonNode> listAll();

	List<JsonNode> listAllByCategoryId(Integer categoryId);

}

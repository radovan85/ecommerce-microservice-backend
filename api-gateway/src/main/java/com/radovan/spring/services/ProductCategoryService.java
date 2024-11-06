package com.radovan.spring.services;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public interface ProductCategoryService {

	String addCategory(JsonNode category);

	JsonNode getCategoryById(Integer categoryId);

	String updateCategory(JsonNode category, Integer categoryId);

	String deleteCategory(Integer categoryId);

	List<JsonNode> listAll();
}

package com.radovan.spring.services;

import java.util.List;

import com.radovan.spring.dto.ProductCategoryDto;

public interface ProductCategoryService {

	ProductCategoryDto addCategory(ProductCategoryDto category);

	ProductCategoryDto getCategoryById(Integer categoryId);

	ProductCategoryDto updateCategory(ProductCategoryDto category, Integer categoryId);

	void deleteCategory(Integer categoryId);

	List<ProductCategoryDto> listAll();
}
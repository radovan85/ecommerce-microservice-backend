package com.radovan.spring.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.ProductCategoryDto;
import com.radovan.spring.entity.ProductCategoryEntity;
import com.radovan.spring.exceptions.ExistingInstanceException;
import com.radovan.spring.exceptions.InstanceUndefinedException;
import com.radovan.spring.repositories.ProductCategoryRepository;
import com.radovan.spring.services.ProductCategoryService;
import com.radovan.spring.services.ProductService;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

	@Autowired
	private ProductCategoryRepository categoryRepository;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private ProductService productService;

	@Override
	@Transactional
	public ProductCategoryDto addCategory(ProductCategoryDto category) {
		Optional<ProductCategoryEntity> categoryOptional = categoryRepository.findByName(category.getName());
		if (categoryOptional.isPresent()) {
			throw new ExistingInstanceException(new Error("This category already exists!"));
		}
		ProductCategoryEntity categoryEntity = tempConverter.categoryDtoToEntity(category);
		ProductCategoryEntity storedCategory = categoryRepository.save(categoryEntity);
		return tempConverter.categoryEntityToDto(storedCategory);
	}

	@Override
	@Transactional(readOnly = true)
	public ProductCategoryDto getCategoryById(Integer categoryId) {
		ProductCategoryEntity categoryEntity = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new InstanceUndefinedException(new Error("The category has not been found")));
		return tempConverter.categoryEntityToDto(categoryEntity);
	}

	@Override
	@Transactional
	public ProductCategoryDto updateCategory(ProductCategoryDto category, Integer categoryId) {
		ProductCategoryDto currentCategory = getCategoryById(categoryId);
		Optional<ProductCategoryEntity> categoryOptional = categoryRepository.findByName(category.getName());
		if (categoryOptional.isPresent()) {
			if (categoryOptional.get().getProductCategoryId() != categoryId) {
				throw new ExistingInstanceException(new Error("This category already exists!"));
			}
		}
		category.setProductCategoryId(currentCategory.getProductCategoryId());
		ProductCategoryEntity updatedCategory = categoryRepository
				.saveAndFlush(tempConverter.categoryDtoToEntity(category));
		return tempConverter.categoryEntityToDto(updatedCategory);
	}

	@Override
	@Transactional
	public void deleteCategory(Integer categoryId) {
		getCategoryById(categoryId);
		productService.deleteProductsByCategoryId(categoryId);
		categoryRepository.deleteById(categoryId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductCategoryDto> listAll() {
		List<ProductCategoryEntity> allCategories = categoryRepository.findAll();
		return allCategories.stream().map(tempConverter::categoryEntityToDto).collect(Collectors.toList());
	}
}

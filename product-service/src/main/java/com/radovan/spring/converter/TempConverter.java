package com.radovan.spring.converter;

import java.text.DecimalFormat;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.radovan.spring.dto.ProductCategoryDto;
import com.radovan.spring.dto.ProductDto;
import com.radovan.spring.dto.ProductImageDto;
import com.radovan.spring.entity.ProductCategoryEntity;
import com.radovan.spring.entity.ProductEntity;
import com.radovan.spring.entity.ProductImageEntity;
import com.radovan.spring.repositories.ProductCategoryRepository;
import com.radovan.spring.repositories.ProductImageRepository;
import com.radovan.spring.repositories.ProductRepository;

@Component
public class TempConverter {

	private ModelMapper mapper;

	private ProductImageRepository imageRepository;

	private ProductCategoryRepository categoryRepository;

	private ProductRepository productRepository;

	private final DecimalFormat decfor = new DecimalFormat("0.00");

	public ProductDto productEntityToDto(ProductEntity product) {
		ProductDto returnValue = mapper.map(product, ProductDto.class);
		returnValue.setProductPrice(Float.valueOf(decfor.format(returnValue.getProductPrice())));
		returnValue.setDiscount(Float.valueOf(decfor.format(returnValue.getDiscount())));

		Optional<ProductImageEntity> imageOptional = Optional.ofNullable(product.getImage());
		if (imageOptional.isPresent()) {
			returnValue.setImageId(imageOptional.get().getId());
		}

		Optional<ProductCategoryEntity> categoryOptional = Optional.ofNullable(product.getProductCategory());
		if (categoryOptional.isPresent()) {
			returnValue.setProductCategoryId(categoryOptional.get().getProductCategoryId());
		}
		return returnValue;

	}

	public ProductEntity productDtoToEntity(ProductDto product) {
		ProductEntity returnValue = mapper.map(product, ProductEntity.class);
		returnValue.setProductPrice(Float.valueOf(decfor.format(returnValue.getProductPrice())));
		returnValue.setDiscount(Float.valueOf(decfor.format(returnValue.getDiscount())));

		Optional<Integer> imageIdOptional = Optional.ofNullable(product.getImageId());
		if (imageIdOptional.isPresent()) {
			Integer imageId = imageIdOptional.get();
			ProductImageEntity imageEntity = imageRepository.findById(imageId).orElse(null);
			returnValue.setImage(imageEntity);
		}

		Optional<Integer> categoryIdOptional = Optional.ofNullable(product.getProductCategoryId());
		if (categoryIdOptional.isPresent()) {
			Integer categoryId = categoryIdOptional.get();
			ProductCategoryEntity categoryEntity = categoryRepository.findById(categoryId).orElse(null);
			returnValue.setProductCategory(categoryEntity);
		}
		return returnValue;

	}

	public ProductImageDto productImageEntityToDto(ProductImageEntity image) {
		ProductImageDto returnValue = mapper.map(image, ProductImageDto.class);
		Optional<ProductEntity> productOptional = Optional.ofNullable(image.getProduct());
		if (productOptional.isPresent()) {
			returnValue.setProductId(productOptional.get().getProductId());
		}
		return returnValue;

	}

	public ProductImageEntity productImageDtoToEntity(ProductImageDto image) {

		// TODO Auto-generated method stub

		ProductImageEntity returnValue = mapper.map(image, ProductImageEntity.class);
		Optional<Integer> productIdOptional = Optional.ofNullable(image.getProductId());
		if (productIdOptional.isPresent()) {
			Integer productId = productIdOptional.get();
			ProductEntity productEntity = productRepository.findById(productId).orElse(null);
			if (productEntity != null) {
				returnValue.setProduct(productEntity);
			}
		}

		return returnValue;

	}

	public ProductCategoryDto categoryEntityToDto(ProductCategoryEntity category) {
		return mapper.map(category, ProductCategoryDto.class);

	}

	public ProductCategoryEntity categoryDtoToEntity(ProductCategoryDto category) {
		return mapper.map(category, ProductCategoryEntity.class);

	}

	@Autowired

	private void initializeAll(ModelMapper mapper, ProductImageRepository imageRepository,

			ProductCategoryRepository categoryRepository, ProductRepository productRepository) {

		this.mapper = mapper;
		this.imageRepository = imageRepository;
		this.categoryRepository = categoryRepository;
		this.productRepository = productRepository;

	}

}

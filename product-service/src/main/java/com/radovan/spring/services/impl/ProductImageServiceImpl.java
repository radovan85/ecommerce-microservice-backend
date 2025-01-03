package com.radovan.spring.services.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.ProductDto;
import com.radovan.spring.dto.ProductImageDto;
import com.radovan.spring.entity.ProductImageEntity;
import com.radovan.spring.exceptions.FileUploadException;
import com.radovan.spring.repositories.ProductImageRepository;
import com.radovan.spring.services.ProductImageService;
import com.radovan.spring.services.ProductService;
import com.radovan.spring.utils.FileValidator;

@Service
public class ProductImageServiceImpl implements ProductImageService {

	@Autowired
	private ProductImageRepository imageRepository;

	@Autowired
	private ProductService productService;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private FileValidator fileValidator;

	@Override
	@Transactional
	public ProductImageDto addImage(MultipartFile file, Integer productId) {
		ProductDto product = productService.getProductById(productId);
		fileValidator.validateFile(file);
		Optional<ProductImageEntity> imageOptional = imageRepository.findByProductId(productId);
		if (imageOptional.isPresent()) {
			deleteImage(imageOptional.get().getId());
		}
		try {
			ProductImageDto image = new ProductImageDto();
			image.setProductId(productId);
			image.setName(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));
			image.setContentType(file.getContentType());
			image.setSize(file.getSize());
			image.setData(file.getBytes());

			Optional<Integer> imageIdOptional = Optional.ofNullable(product.getImageId());
			if (imageIdOptional.isPresent()) {
				image.setId(imageIdOptional.get());
			}

			ProductImageEntity imageEntity = tempConverter.productImageDtoToEntity(image);
			ProductImageEntity storedImage = imageRepository.save(imageEntity);

			return tempConverter.productImageEntityToDto(storedImage);
		} catch (Exception e) {
			throw new FileUploadException(new Error("Failed to upload file: " + e.getMessage()));
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductImageDto> listAll() {
		// TODO Auto-generated method stub
		List<ProductImageEntity> allImages = imageRepository.findAll();
		return allImages.stream().map(tempConverter::productImageEntityToDto).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void deleteImage(Integer imageId) {
		// TODO Auto-generated method stub
		imageRepository.deleteById(imageId);
		imageRepository.flush();
	}

}

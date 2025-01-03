package com.radovan.spring.services;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.radovan.spring.dto.ProductImageDto;

public interface ProductImageService {

	ProductImageDto addImage(MultipartFile file, Integer productId);
	
	void deleteImage(Integer imageId);

	List<ProductImageDto> listAll();
}

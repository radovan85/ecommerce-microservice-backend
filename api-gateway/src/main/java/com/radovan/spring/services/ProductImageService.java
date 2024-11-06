package com.radovan.spring.services;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.JsonNode;

public interface ProductImageService {

	String addImage(MultipartFile file, Integer productId);

	List<JsonNode> listAll();
}

package com.radovan.spring.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.radovan.spring.converter.DeserializeConverter;
import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.ProductDto;
import com.radovan.spring.entity.ProductEntity;
import com.radovan.spring.exceptions.InstanceUndefinedException;
import com.radovan.spring.repositories.ProductRepository;
import com.radovan.spring.services.ProductCategoryService;
import com.radovan.spring.services.ProductService;
import com.radovan.spring.utils.ServiceUrlProvider;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private ProductCategoryService categoryService;

	@Autowired
	private DeserializeConverter deserializeConverter;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ServiceUrlProvider urlProvider;

	@Override
	@Transactional
	public ProductDto addProduct(ProductDto product) {
		categoryService.getCategoryById(product.getProductCategoryId());
		ProductEntity storedProduct = productRepository.save(tempConverter.productDtoToEntity(product));
		return tempConverter.productEntityToDto(storedProduct);
	}

	@Override
	@Transactional(readOnly = true)
	public ProductDto getProductById(Integer productId) {
		ProductEntity productEntity = productRepository.findById(productId)
				.orElseThrow(() -> new InstanceUndefinedException(new Error("The product has not been found!")));
		return tempConverter.productEntityToDto(productEntity);
	}

	@Override
	@Transactional
	public ProductDto updateProduct(ProductDto product, Integer productId) {
		categoryService.getCategoryById(product.getProductCategoryId());

		ProductDto currentProduct = getProductById(productId);
		List<JsonNode> allCartItems = deserializeConverter
				.getJsonNodeList(urlProvider.getCartServiceUrl() + "/items/allItemsByProductId/" + productId);

		product.setProductId(currentProduct.getProductId());
		if (currentProduct.getImageId() != null) {
			product.setImageId(currentProduct.getImageId());
		}

		ProductEntity updatedProduct = productRepository.saveAndFlush(tempConverter.productDtoToEntity(product));

		if (!allCartItems.isEmpty()) {
			allCartItems.forEach((item) -> {
				Map<String, Object> itemMap = deserializeConverter.deserializeJson(item.toString());
				String itemId = (String) itemMap.get("cartItemId");
				Float quantity = Float.valueOf((String) itemMap.get("quantity")); // Uzimanje kvantiteta

				// Izračunavanje cene
				Float discount = updatedProduct.getDiscount();
				Float productPrice = updatedProduct.getProductPrice(); // Uzmi novu cenu proizvoda
				Float itemPrice = productPrice - ((productPrice * discount) / 100) * quantity;

				// Kreiranje mapu za sent JSON objekat
				Map<String, Object> updatedItemMap = new HashMap<>();
				updatedItemMap.put("cartItemId", itemId);
				updatedItemMap.put("price", itemPrice); // Postavljanje nove cene
				updatedItemMap.put("quantity", quantity); // Ako želite da izmenite i količinu

				ObjectMapper mapper = new ObjectMapper();
				JsonNode cartItemDto = mapper.valueToTree(updatedItemMap);
				// Pozivanje cart-service
				String cartItemUrl = urlProvider.getCartServiceUrl() + "/items/updateItem/" + itemId;
				HttpEntity<JsonNode> requestEntity = new HttpEntity<>(cartItemDto);
				restTemplate.exchange(cartItemUrl, HttpMethod.PUT, requestEntity, Void.class);
			});
		}

		HttpEntity<Void> refreshRequestEntity = new HttpEntity<>(null);
		String cartRefreshUrl = urlProvider.getCartServiceUrl() + "/cart/refreshAllCarts";
		restTemplate.exchange(cartRefreshUrl, HttpMethod.PUT, refreshRequestEntity, Void.class);

		return tempConverter.productEntityToDto(updatedProduct);
	}

	@Override
	@Transactional
	public void deleteProduct(Integer productId) {
		getProductById(productId);
		HttpEntity<Void> requestEntity = new HttpEntity<>(null);
		String deleteItemsUrl = urlProvider.getCartServiceUrl() + "/items/clearAllByProductId/" + productId;
		restTemplate.exchange(deleteItemsUrl, HttpMethod.DELETE, requestEntity, Void.class);
		productRepository.deleteById(productId);
		productRepository.flush();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductDto> listAll() {
		List<ProductEntity> allProducts = productRepository.findAll();
		return allProducts.stream().map(tempConverter::productEntityToDto).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductDto> listAllByCategoryId(Integer categoryId) {
		List<ProductEntity> allProducts = productRepository.findAllByCategoryId(categoryId);
		return allProducts.stream().map(tempConverter::productEntityToDto).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void deleteProductsByCategoryId(Integer categoryId) {
		List<ProductDto> allProducts = listAllByCategoryId(categoryId);
		allProducts.forEach((product) -> {
			deleteProduct(product.getProductId());
		});
	}

	@Override
	@Transactional
	public ProductDto updateProduct(JsonNode product, Integer productId) {
		// TODO Auto-generated method stub
		ProductDto existingProduct = getProductById(productId);
		Map<String, Object> productMap = deserializeConverter.deserializeJson(product.toString());
		existingProduct.setUnitStock(Integer.valueOf(productMap.get("unitStock").toString()));
		ProductEntity updatedProduct = productRepository
				.saveAndFlush(tempConverter.productDtoToEntity(existingProduct));
		return tempConverter.productEntityToDto(updatedProduct);

	}

}

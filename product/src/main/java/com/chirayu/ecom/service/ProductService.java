package com.chirayu.ecom.service;

import com.chirayu.ecom.dto.ProductRequest;
import com.chirayu.ecom.dto.ProductResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    ProductResponse createProduct(ProductRequest productRequest);
    ProductResponse updateProduct(Long id, ProductRequest productRequest);
    Page<ProductResponse> getAllProducts(int page, int size);
    boolean deleteProduct(Long id);
    List<ProductResponse> searchProducts(String keyword);
    Optional<ProductResponse> getProductById(Long productId);
}

package com.chirayu.ecom.service;

import com.chirayu.ecom.dto.ProductRequest;
import com.chirayu.ecom.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface ProductService {
    ProductResponse createProduct(ProductRequest productRequest);
    ProductResponse updateProduct(Long id, ProductRequest productRequest);
    Page<ProductResponse> getAllProducts(int page, int size);
    boolean deleteProduct(Long id);
    Page<ProductResponse> searchProducts(String keyword,int page, int size);
    Optional<ProductResponse> getProductById(Long productId);
    void reduceStock(Long productId, int quantity);
    void restoreStock(Long productId, int quantity);
    void saveFromExcel(MultipartFile file);
}

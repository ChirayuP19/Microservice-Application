package com.chirayu.ecom.service;

import com.chirayu.ecom.dto.ProductRequest;
import com.chirayu.ecom.dto.ProductResponse;
import com.chirayu.ecom.entity.Product;
import com.chirayu.ecom.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = new Product();
        updateProductFromRequest(product,productRequest);
        Product saveProduct = productRepository.save(product);
        return mapToProductResponse(saveProduct);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    updateProductFromRequest(existingProduct, productRequest);
                    Product savedProduct = productRepository.save(existingProduct);
                    return mapToProductResponse(savedProduct);
                })
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found or Product Out of Stock with id: " + id));
    }

    @Override
    public Page<ProductResponse> getAllProducts(int page, int size) {
        Pageable pageable= PageRequest.of(page,size);
        return productRepository.findByActiveTrue(pageable)
                .map(this::mapToProductResponse);
    }

    @Override
    public boolean deleteProduct(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setActive(false);
                    productRepository.save(product);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword)
                .stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    @Override
    public Optional<ProductResponse> getProductById(Long productId) {
        return productRepository.findByIdAndActiveTrue(productId)
                .map(this::mapToProductResponse);
    }

    private ProductResponse mapToProductResponse(Product saveProduct) {
        ProductResponse product = new ProductResponse();
        product.setId(saveProduct.getId());
        product.setName(saveProduct.getName());
        product.setPrice(saveProduct.getPrice());
        product.setCategory(saveProduct.getCategory());
        product.setActive(saveProduct.getActive());
        product.setDescription(saveProduct.getDescription());
        product.setImageUrl(saveProduct.getImageUrl());
        product.setStockQuantity(saveProduct.getStockQuantity());
        return product;


    }

    private void updateProductFromRequest(Product product,ProductRequest productRequest) {
        product.setName(productRequest.getName());
        product.setCategory(productRequest.getCategory());
        product.setDescription(productRequest.getDescription());
        product.setStockQuantity(productRequest.getStockQuantity());
        product.setImageUrl(productRequest.getImageUrl());
        product.setPrice(productRequest.getPrice());
    }
}

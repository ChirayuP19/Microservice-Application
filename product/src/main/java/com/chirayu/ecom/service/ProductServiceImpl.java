package com.chirayu.ecom.service;

import com.chirayu.ecom.dto.ProductRequest;
import com.chirayu.ecom.dto.ProductResponse;
import com.chirayu.ecom.elasticsearch.ProductDocument;
import com.chirayu.ecom.elasticsearch.ProductSearchRepository;
import com.chirayu.ecom.entity.Product;
import com.chirayu.ecom.helper.Helper;
import com.chirayu.ecom.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = new Product();
        updateProductFromRequest(product,productRequest);
        Product saveProduct = productRepository.save(product);
        productSearchRepository.save(mapToProductDocument(saveProduct));
        return mapToProductResponse(saveProduct);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    updateProductFromRequest(existingProduct, productRequest);
                    Product savedProduct = productRepository.save(existingProduct);
                    productSearchRepository.save(mapToProductDocument(savedProduct));
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
                    productSearchRepository.deleteById(String.valueOf(id));
                    return true;
                })
                .orElse(false);
    }

    @Override
    public Page<ProductResponse> searchProducts(String keyword,int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productSearchRepository
                .findByNameContainingOrDescriptionContainingOrCategoryContaining(
                        keyword, keyword, keyword,pageable)
                .map(this::mapDocumentToProductResponse);
    }

    @Override
    public Optional<ProductResponse> getProductById(Long productId) {
        return productRepository.findByIdAndActiveTrue(productId)
                .map(this::mapToProductResponse);
    }

    @Override
    public void reduceStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found with id: " + productId));

        if (product.getStockQuantity() < quantity) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Insufficient stock for product: " + productId);
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        Product savedProduct = productRepository.save(product);

        productSearchRepository.save(mapToProductDocument(savedProduct));
    }

    @Override
    public void restoreStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException
                        (HttpStatus.NOT_FOUND, "Product not found with id: " + productId));

        product.setStockQuantity(product.getStockQuantity()+quantity);
        Product save = productRepository.save(product);
        productSearchRepository.save(mapToProductDocument(save));
    }

    @Override
    public void saveFromExcel(MultipartFile file) {
        try {
            List<Product> products = Helper.convertExcelToListOfProduct(file.getInputStream());
            List<Product> saveProduct = productRepository.saveAll(products);
            List<ProductDocument> documents = saveProduct.stream()
                    .map(this::mapToProductDocument)
                    .toList();
            productSearchRepository.saveAll(documents);

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private ProductResponse mapDocumentToProductResponse(ProductDocument doc) {
        ProductResponse response = new ProductResponse();
        response.setId(Long.valueOf(doc.getId()));
        response.setName(doc.getName());
        response.setDescription(doc.getDescription());
        response.setCategory(doc.getCategory());
        response.setPrice(BigDecimal.valueOf(doc.getPrice()));
        response.setStockQuantity(doc.getStockQuantity());
        response.setImageUrl(doc.getImageUrl());
        return response;
    }

    private ProductDocument mapToProductDocument(Product product) {
        return ProductDocument.builder()
                .id(String.valueOf(product.getId()))
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .price(product.getPrice().doubleValue())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .build();
    }
}

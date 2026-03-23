package com.chirayu.ecom.controller;

import com.chirayu.ecom.dto.ProductRequest;
import com.chirayu.ecom.dto.ProductResponse;
import com.chirayu.ecom.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping("")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,@RequestBody ProductRequest productRequest){
        return  ResponseEntity.status(HttpStatus.OK).body(productService.updateProduct(id,productRequest));
    }

    @GetMapping("")
    public ResponseEntity<Page<ProductResponse>>getAllData(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.status(HttpStatus.OK).body(productService.getAllProducts(page,size));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        boolean b = productService.deleteProduct(id);
        return b ? ResponseEntity.noContent().build(): ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(@RequestParam String keyword,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.status(HttpStatus.OK).body(productService.searchProducts(keyword,page,size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse>getProductById(@PathVariable("id") Long productId){
        return productService.getProductById(productId)
                .map(ResponseEntity::ok)
                .orElseGet(()->ResponseEntity.notFound().build());
    }
}

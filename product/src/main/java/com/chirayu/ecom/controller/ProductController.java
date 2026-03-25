package com.chirayu.ecom.controller;

import com.chirayu.ecom.dto.ProductRequest;
import com.chirayu.ecom.dto.ProductResponse;
import com.chirayu.ecom.entity.Product;
import com.chirayu.ecom.helper.Helper;
import com.chirayu.ecom.repository.ProductRepository;
import com.chirayu.ecom.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {
    
    private final ProductService productService;
    private final ProductRepository productRepository;

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

    @PatchMapping("/{productId}/reduce-stock")
    public ResponseEntity<Void> reduceStock(
            @PathVariable("productId") Long productId,
            @RequestParam int quantity){

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStockQuantity() < quantity) {
            return ResponseEntity.badRequest().build();
        }
        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{productId}/restore-stock")
    public ResponseEntity<Void> restoreStock(
            @PathVariable Long productId,
            @RequestParam int quantity) {
        productService.restoreStock(productId, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bulk-upload")
    public ResponseEntity<?> bulkUpload(@RequestParam("file")MultipartFile file){

        if ( Helper.checkFileContentType(file)) {
            productService.saveFromExcel(file);

            return ResponseEntity.ok(Map.of("message","File is uploaded and data is saved to database."));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload excel file :");
    }

}

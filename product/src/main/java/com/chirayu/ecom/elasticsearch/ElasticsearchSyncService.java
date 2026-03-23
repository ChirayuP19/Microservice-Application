package com.chirayu.ecom.elasticsearch;

import com.chirayu.ecom.entity.Product;
import com.chirayu.ecom.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchSyncService {
    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;

    public void syncAllProducts(){
        log.info("Starting Elasticsearch sync...");
        List<Product> productList = productRepository.findAll();
        List<ProductDocument> documents = productList
                .stream().map(this::mapToProductDocument).
                toList();
        productSearchRepository.saveAll(documents);
        log.info("Elasticsearch sync completed! {} products synced.", documents.size());
    }

    public ProductDocument mapToProductDocument(Product product){
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

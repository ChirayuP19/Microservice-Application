package com.chirayu.ecom.elasticsearch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products/sync")
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchSyncController {
    private final ElasticsearchSyncService elasticsearchSyncService;

    @PostMapping
    public ResponseEntity<String> syncToElasticsearch() {
        elasticsearchSyncService.syncAllProducts();
        return ResponseEntity.ok("All products synced to Elasticsearch successfully!");
    }
}

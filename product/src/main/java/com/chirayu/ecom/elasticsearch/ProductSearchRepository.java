package com.chirayu.ecom.elasticsearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument,String> {

    List<ProductDocument> findByNameContaining(String name);

    Page<ProductDocument> findByNameContainingOrDescriptionContainingOrCategoryContaining(
            String name, String description, String category, Pageable pageable);
}

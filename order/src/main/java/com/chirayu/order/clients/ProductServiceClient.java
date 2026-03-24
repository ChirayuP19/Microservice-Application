package com.chirayu.order.clients;

import com.chirayu.order.dto.ProductResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;

@HttpExchange
public interface ProductServiceClient {

    @GetExchange("/api/v1/products/{id}")
    ProductResponse getProductDetails(@PathVariable("id")Long productId);

    @PatchExchange("/api/v1/products/{productId}/reduce-stock")
    void reduceStock(@PathVariable Long productId,
                     @RequestParam int quantity);

    @PatchExchange("/api/products/{productId}/restore-stock")
    void restoreStock(
            @PathVariable Long productId,
            @RequestParam int quantity
    );
}

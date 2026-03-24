package com.chirayu.ecom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEvent {
    private Long orderId;
    private String userId;
    private String userEmail;
    private String userName;
    private String userPhone;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
}

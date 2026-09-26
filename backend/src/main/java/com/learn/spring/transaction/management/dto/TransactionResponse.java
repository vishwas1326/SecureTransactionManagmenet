package com.learn.spring.transaction.management.dto;


import com.learn.spring.transaction.management.entity.TransactionStatus;
import com.learn.spring.transaction.management.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        String transactionReference,
        TransactionType transactionType,
        BigDecimal amount,
        String description,
        TransactionStatus transactionStatus,
        String createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

package com.learn.spring.transaction.management.events;

import java.math.BigDecimal;

public record TransactionCreatedEvent(
        Long transactionId,
        String transactionReference,
        String transactionType,
        BigDecimal amount,
        String status,
        String createdBy

) {


}

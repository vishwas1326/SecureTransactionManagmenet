package com.learn.spring.transaction.management.mapper;

import com.learn.spring.transaction.management.dto.TransactionRequest;
import com.learn.spring.transaction.management.dto.TransactionResponse;
import com.learn.spring.transaction.management.entity.Transaction;
import com.learn.spring.transaction.management.entity.TransactionStatus;

public final class TransactionMapper {

    public static Transaction toEntity(TransactionRequest request){

        Transaction transaction = new Transaction();

        transaction.setTransactionType(request.transactionType());
        transaction.setAmount(request.amount());
        transaction.setDescription(request.description());
        transaction.setCreatedBy(request.createdBy());
        transaction.setTransactionStatus(request.status() == null ? TransactionStatus.PENDING : request.status());
        return transaction;
    }

    public static TransactionResponse toResponse(Transaction transaction){

        return new TransactionResponse(
                transaction.getId(),
                transaction.getTransactionReference(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getTransactionStatus(),
                transaction.getCreatedBy(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );

    }


}

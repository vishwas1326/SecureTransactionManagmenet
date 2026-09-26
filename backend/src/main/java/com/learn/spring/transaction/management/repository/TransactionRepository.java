package com.learn.spring.transaction.management.repository;

import com.learn.spring.transaction.management.entity.Transaction;
import com.learn.spring.transaction.management.entity.TransactionStatus;
import com.learn.spring.transaction.management.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;

public interface TransactionRepository extends JpaRepository<Transaction,Long>, JpaSpecificationExecutor<Transaction> {

    Page<Transaction> findByTransactionStatus(TransactionStatus transactionStatus, Pageable pageable);

    Page<Transaction>
    findByTransactionReferenceContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCreatedByContainingIgnoreCase(

            String transactionReference,

            String description,

            String createdBy,

            Pageable pageable
    );

    Page<Transaction>
    findByTransactionStatusAndTransactionTypeAndAmountBetweenAndCreatedByContainingIgnoreCase(
            TransactionStatus status,
            TransactionType type,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String createdBy,
            Pageable pageable
    );
}

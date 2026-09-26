package com.learn.spring.transaction.management.dto;

import com.learn.spring.transaction.management.entity.TransactionStatus;
import com.learn.spring.transaction.management.entity.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TransactionRequest(

    @NotNull(message = "Transcation type is required")
    TransactionType transactionType,

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount should be greater than zero")
    BigDecimal amount,

    @Size(max = 250,message = "Description cannot exceed 250 characters")
    String description,

    TransactionStatus status,
    @NotBlank(message = "Created by is required")
    String createdBy

){

}

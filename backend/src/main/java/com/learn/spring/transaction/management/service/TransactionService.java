package com.learn.spring.transaction.management.service;

import com.learn.spring.transaction.management.dto.PagedResponse;
import com.learn.spring.transaction.management.dto.TransactionRequest;
import com.learn.spring.transaction.management.dto.TransactionResponse;
import com.learn.spring.transaction.management.entity.Transaction;
import com.learn.spring.transaction.management.entity.TransactionStatus;
import com.learn.spring.transaction.management.entity.TransactionType;
import com.learn.spring.transaction.management.events.TransactionCreatedEvent;
import com.learn.spring.transaction.management.exception.ResourceNotFoundException;
import com.learn.spring.transaction.management.kafka.TransactionProducer;
import com.learn.spring.transaction.management.mapper.TransactionMapper;
import com.learn.spring.transaction.management.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionProducer transactionProducer;

    public TransactionService(TransactionRepository repository,TransactionProducer transactionProducer) {
        this.transactionRepository = repository;
        this.transactionProducer = transactionProducer;
    }

    @Transactional
    public TransactionResponse create(TransactionRequest request){
        Transaction transaction = TransactionMapper.toEntity(request);

        String reference = "TXN-"+ UUID.randomUUID();

        transaction.setTransactionReference(reference);
        Transaction result = transactionRepository.save(transaction);
        TransactionCreatedEvent event = new TransactionCreatedEvent(transaction.getId(),
                transaction.getTransactionReference(),transaction.getTransactionType().name(),transaction.getAmount(),transaction.getTransactionStatus().name()
                ,transaction.getCreatedBy());

        transactionProducer.publishTransactionCreated(event);

        return TransactionMapper.toResponse(result);
    }

    public PagedResponse<TransactionResponse> getAll(Pageable pageable){

        Page<Transaction> transactionPage = transactionRepository.findAll(pageable);

        return toPagedResponse(transactionPage);
    }

    public  TransactionResponse getById(Long id){
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        return TransactionMapper.toResponse(transaction);
    }

    @Transactional
    public TransactionResponse update(Long id,TransactionRequest request){
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));


        transaction.setTransactionType(
                request.transactionType()
        );

        transaction.setAmount(
                request.amount()
        );

        transaction.setDescription(
                request.description()
        );

        transaction.setCreatedBy(
                request.createdBy()
        );
        if (request.status() != null) {

            transaction.setTransactionStatus(
                    request.status()
            );
        }
        Transaction updated =
                transactionRepository.save(transaction);

        return TransactionMapper.toResponse(updated);
    }

    @Transactional
    public void delete(Long id){
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction not Found unable to delete"));
    }

    public PagedResponse<TransactionResponse> getByStatus(TransactionStatus transactionStatus, Pageable pageable){
        Page<Transaction> transactionPage = transactionRepository.findByTransactionStatus(transactionStatus,pageable);

        return toPagedResponse(transactionPage);

    }

    private PagedResponse<TransactionResponse> toPagedResponse(Page<Transaction> transactionPage) {

        List<TransactionResponse> content = transactionPage
                .getContent()
                .stream()
                .map(TransactionMapper::toResponse)
                .toList();

        return new PagedResponse<>(
                content,
                transactionPage.getTotalPages(),
                transactionPage.getSize(),
                transactionPage.getTotalElements(),
                transactionPage.getTotalPages(),
                transactionPage.isFirst(),
                transactionPage.isLast()
        );

    }

    public PagedResponse<TransactionResponse> search(String search, Pageable pageable){
        Page<Transaction> transactionPage = transactionRepository.findByTransactionReferenceContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCreatedByContainingIgnoreCase(search,search,search,pageable);
        return toPagedResponse(transactionPage);
    }

    public PagedResponse<TransactionResponse> advanceSearch(TransactionStatus status, TransactionType type, BigDecimal minAmount,BigDecimal maxAmount,String keyword,Pageable pageable){
        Page<Transaction> transactionPage = transactionRepository.findByTransactionStatusAndTransactionTypeAndAmountBetweenAndCreatedByContainingIgnoreCase(status,type,minAmount,maxAmount,keyword,pageable);
        return toPagedResponse(transactionPage);
    }
}

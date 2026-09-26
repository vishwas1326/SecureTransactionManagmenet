package com.learn.spring.transaction.management.controller;

import com.learn.spring.transaction.management.dto.PagedResponse;
import com.learn.spring.transaction.management.dto.TransactionRequest;
import com.learn.spring.transaction.management.dto.TransactionResponse;
import com.learn.spring.transaction.management.entity.TransactionStatus;
import com.learn.spring.transaction.management.entity.TransactionType;
import com.learn.spring.transaction.management.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService service){
        this.transactionService = service;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionRequest request){
        TransactionResponse transactionResponse = this.transactionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponse);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<TransactionResponse>> getAll(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdAt",
                    direction =  Sort.Direction.DESC
            )Pageable pageable
            ) {

        return ResponseEntity.ok(transactionService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                transactionService.getById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> update(
            @PathVariable Long id,
            @Valid
            @RequestBody TransactionRequest request
    ) {

        return ResponseEntity.ok(
                transactionService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<PagedResponse<TransactionResponse>> getStatus(@PathVariable TransactionStatus status,@PageableDefault(
            page = 0,
            size = 10,
            sort = "createdAt",
            direction = Sort.Direction.DESC
    ) Pageable pageable){
        return ResponseEntity.ok(transactionService.getByStatus(status,pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<TransactionResponse>> search(@RequestParam String keyword,@PageableDefault(
            page = 0,
            size = 10,
            sort = "createdAt",
            direction = Sort.Direction.DESC
    ) Pageable pageable){
        return ResponseEntity.ok(transactionService.search(keyword,pageable));
    }

    @GetMapping("/advancesearch")
    public ResponseEntity<PagedResponse<TransactionResponse>> advancesearch(@RequestParam(required = false) TransactionStatus status, @RequestParam(required = false) TransactionType type,
            @RequestParam BigDecimal minAmount,@RequestParam BigDecimal maxAmount,@RequestParam String keyword, @PageableDefault(
            page = 0,
            size = 10,
            sort = "createdAt",
            direction = Sort.Direction.DESC
    ) Pageable pageable){
        return ResponseEntity.ok(transactionService.advanceSearch(status,type,minAmount,maxAmount,keyword,pageable));
    }




}

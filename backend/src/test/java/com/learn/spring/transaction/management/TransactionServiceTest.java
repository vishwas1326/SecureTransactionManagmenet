package com.learn.spring.transaction.management;

import com.learn.spring.transaction.management.dto.TransactionResponse;
import com.learn.spring.transaction.management.entity.Transaction;
import com.learn.spring.transaction.management.repository.TransactionRepository;
import com.learn.spring.transaction.management.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void shouldReturnTransaction(){

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setTransactionReference("TXN1001");

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        // ACT
        TransactionResponse response =
                transactionService.getById(1L);


        // ASSERT
        assertEquals(
                "TXN1001",
                response.transactionReference()
        );

        verify(transactionRepository)
                .findById(1L);
    }

}

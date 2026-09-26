package com.learn.spring.transaction.management;

import com.learn.spring.transaction.management.repository.TransactionRepository;
import com.learn.spring.transaction.management.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringCrudApplicationTests {

	@Mock
	private TransactionRepository transactionRepository;

	@InjectMocks
	private TransactionService transactionService;

	@Test
	void contextLoads() {
	}

	@Test
	void shouldReturnTransaction(){

	}

}

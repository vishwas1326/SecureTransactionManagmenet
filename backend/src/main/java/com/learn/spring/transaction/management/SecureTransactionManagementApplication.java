package com.learn.spring.transaction.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class SecureTransactionManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecureTransactionManagementApplication.class, args);
	}

}

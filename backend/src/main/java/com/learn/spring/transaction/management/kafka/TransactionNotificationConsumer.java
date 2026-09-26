package com.learn.spring.transaction.management.kafka;

import com.learn.spring.transaction.management.events.TransactionCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionNotificationConsumer {

    @KafkaListener(topics = "transaction-created", groupId = "transaction-notification-group")
    public void consume(TransactionCreatedEvent event){
        System.out.println("Transaction Event received");
        System.out.println("Reference: " + event.transactionReference());
        System.out.println("Amount: " + event.amount());
        System.out.println("Created By: " + event.createdBy());
    }
}

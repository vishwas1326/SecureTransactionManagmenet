package com.learn.spring.transaction.management.kafka;

import com.learn.spring.transaction.management.events.TransactionCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionProducer {

    private final KafkaTemplate<String, TransactionCreatedEvent> kafkaTemplate;

    public TransactionProducer(KafkaTemplate<String, TransactionCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishTransactionCreated(TransactionCreatedEvent event){
        kafkaTemplate.send("transaction-created",event.transactionReference(),event);
    }
}

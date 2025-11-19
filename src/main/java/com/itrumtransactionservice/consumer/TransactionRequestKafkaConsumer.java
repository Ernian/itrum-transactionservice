package com.itrumtransactionservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itrumtransactionservice.dto.TransactionRequest;
import com.itrumtransactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionRequestKafkaConsumer {
    private final ObjectMapper objectMapper;
    private final TransactionService transactionService;

    @KafkaListener(topics = "${spring.kafka.topic.transaction-request.name}")
    public void consumeTransactionRequest(String message) {
        TransactionRequest transactionRequest = deserializeRequest(message);
        log.debug("Received TransactionRequest : {}", transactionRequest);

        transactionService.consumeTransactionRequest(transactionRequest);
    }

    private TransactionRequest deserializeRequest(String message) {
        try {
            return objectMapper.readValue(message, TransactionRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
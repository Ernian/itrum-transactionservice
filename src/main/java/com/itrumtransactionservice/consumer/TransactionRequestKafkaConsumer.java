package com.itrumtransactionservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itrumtransactionservice.dto.TransactionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionRequestKafkaConsumer {
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${spring.kafka.topic.transaction-request.name}")
    public void consumeTransactionRequest(String record) {
        try {
            TransactionRequest request = objectMapper.readValue(record, TransactionRequest.class);
            log.debug("Received TransactionRequest : {}", request);

        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
    }
}
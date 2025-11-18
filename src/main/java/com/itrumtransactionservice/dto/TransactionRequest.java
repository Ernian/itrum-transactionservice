package com.itrumtransactionservice.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TransactionRequest(
        String walletId,
        String type,
        BigDecimal amount,
        String transactionId,
        String userId
) {
}
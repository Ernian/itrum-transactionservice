package com.itrumtransactionservice.service;

import com.itrumtransactionservice.dto.TransactionRequest;
import com.itrumtransactionservice.entity.OperationType;
import com.itrumtransactionservice.entity.Transaction;
import com.itrumtransactionservice.entity.Wallet;
import com.itrumtransactionservice.exception.InvalidOperationTypeException;
import com.itrumtransactionservice.exception.WalletNotFoundException;
import com.itrumtransactionservice.repository.TransactionRepository;
import com.itrumtransactionservice.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final RedisService redisService;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public void consumeTransactionRequest(TransactionRequest request) {
        if (requestHasNotProcessedBefore(request)) {
            handleTransactionRequest(request);
            redisService.cacheRequest(request.transactionId());
            redisService.removeWalletFromCache(request.walletId());
        }
    }


    public void handleTransactionRequest(TransactionRequest transactionRequest) {
        UUID walletId = UUID.fromString(transactionRequest.walletId());
        Wallet wallet = findWalletById(walletId);

        if (isOperationAvailable(transactionRequest, wallet)) {
            BigDecimal newBalance = calculateNewBalance(transactionRequest, wallet);
            wallet.setBalance(newBalance);
            walletRepository.save(wallet);
            transactionRepository.save(new Transaction(transactionRequest));
            sendSuccessNotification(transactionRequest);
        } else {
            sendRejectNotification(transactionRequest);
        }
    }

    public boolean requestHasNotProcessedBefore(TransactionRequest request) {
        if (redisService.contains(request.transactionId())) {
            log.info("This request has already been processed, {}", request);
            return false;
        }
        return true;
    }

    public boolean isOperationAvailable(TransactionRequest request, Wallet wallet) {
        OperationType type = operationTypeFromString(request.type());

        if (type == OperationType.DEPOSIT) {
            return true;
        }

        BigDecimal balance = wallet.getBalance();
        return balance.compareTo(request.amount()) >= 0;
    }

    public Wallet findWalletById(UUID walletId) {
        return walletRepository.findByIdWithPessimisticLock(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
    }

    public BigDecimal calculateNewBalance(TransactionRequest request, Wallet wallet) {
        OperationType type = operationTypeFromString(request.type());
        BigDecimal balance = wallet.getBalance();
        BigDecimal amount = request.amount();

        return switch (type) {
            case DEPOSIT -> balance.add(amount);
            case WITHDRAW -> balance.subtract(amount);
        };
    }

    public void sendRejectNotification(TransactionRequest request) {
        log.info("Sending reject notification for request {}", request);
    }

    public void sendSuccessNotification(TransactionRequest transactionRequest) {
        log.info("Sending success notification for request {}", transactionRequest);
    }

    public OperationType operationTypeFromString(String type) {
        try {
            return OperationType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidOperationTypeException("Invalid operation type: " + type);
        }
    }
}
package com.itrumtransactionservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${redis-params.transaction-id-storage}")
    private String transactionIdStorage;

    @Value("${redis-params.transaction-id-ttl-minute:15}")
    private int transactionIdTtlMinute;

    @Value("${redis-params.wallet-key-prefix}")
    private String walletKeyPrefix;

    public void cacheRequest(String transactionId) {
        redisTemplate.opsForSet().add(transactionIdStorage, transactionId);
        redisTemplate.expire(transactionIdStorage, Duration.ofMinutes(transactionIdTtlMinute));
    }

    public boolean contains(String transactionId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(transactionIdStorage, transactionId));
    }

    public void removeWalletFromCache(String walletId) {
        redisTemplate.delete(walletKeyPrefix + walletId);
    }
}
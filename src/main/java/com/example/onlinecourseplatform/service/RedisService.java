package com.example.onlinecourseplatform.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Service class for managing user tokens using Redis as a temporary key-value store.
 */
@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Constructor for RedisService.
     */
    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Saves a JWT token in Redis for the given user ID.
     * The token is stored with a key in the format "TOKEN:{userId}" and expires after 1 day.
     */
    public void saveToken(Long userId, String token) {
        redisTemplate.opsForValue().set("TOKEN:" + userId, token, 1, TimeUnit.DAYS);
    }

    /**
     * Retrieves the JWT token from Redis for the given user ID.
     */
    public String getToken(Long userId) {
        return redisTemplate.opsForValue().get("TOKEN:" + userId);
    }

    /**
     * Deletes the JWT token associated with the given user ID from Redis.
     */
    public void deleteToken(Long userId) {
        redisTemplate.delete("TOKEN:" + userId);
    }
}

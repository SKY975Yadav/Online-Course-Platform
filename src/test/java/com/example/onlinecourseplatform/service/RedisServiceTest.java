package com.example.onlinecourseplatform.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class RedisServiceTest {

    private RedisService redisService;
    private RedisTemplate<String, String> redisTemplate;
    private ValueOperations<String, String> valueOperations;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        // Mock RedisTemplate and ValueOperations
        redisTemplate = mock(RedisTemplate.class);
        valueOperations = mock(ValueOperations.class);

        // Set up the mock behavior
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Create RedisService instance with mocked RedisTemplate
        redisService = new RedisService(redisTemplate);
    }

    @Test
    @DisplayName("Should save token in Redis with expiration of 1 day")
    void shouldSaveTokenToRedis() {
        Long userId = 123L;
        String token = "sample.jwt.token";

        redisService.saveToken(userId, token);

        verify(valueOperations, times(1))
                .set("TOKEN:" + userId, token, 1, TimeUnit.DAYS);
    }

    @Test
    @DisplayName("Should return token from Redis for given user ID")
    void shouldReturnTokenFromRedis() {
        Long userId = 456L;
        String expectedToken = "stored.jwt.token";

        when(valueOperations.get("TOKEN:" + userId)).thenReturn(expectedToken);

        String actualToken = redisService.getToken(userId);

        assertEquals(expectedToken, actualToken);
        verify(valueOperations, times(1)).get("TOKEN:" + userId);
    }

    @Test
    @DisplayName("Should delete token from Redis for given user ID")
    void shouldDeleteTokenFromRedis() {
        Long userId = 789L;

        redisService.deleteToken(userId);

        verify(redisTemplate, times(1)).delete("TOKEN:" + userId);
    }
}

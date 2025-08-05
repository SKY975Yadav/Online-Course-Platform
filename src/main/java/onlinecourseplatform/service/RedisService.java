package onlinecourseplatform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Service class for managing user tokens using Redis as a temporary key-value store.
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String TOKEN_PREFIX = "TOKEN:";

    /**
     * Saves a JWT token in Redis for the given user ID.
     * Token expires after 1 day.
     */
    public void saveToken(Long userId, String token) {
        String key = TOKEN_PREFIX + userId;
        redisTemplate.opsForValue().set(key, token, 1, TimeUnit.DAYS);
        log.info("Token saved in Redis for userId={}", userId);
    }

    /**
     * Retrieves the JWT token from Redis for the given user ID.
     */
    public String getToken(Long userId) {
        String token = redisTemplate.opsForValue().get(TOKEN_PREFIX + userId);
        log.debug("Token fetched from Redis for userId={}", userId);
        return token;
    }

    /**
     * Deletes the JWT token associated with the given user ID from Redis.
     */
    public void deleteToken(Long userId) {
        redisTemplate.delete(TOKEN_PREFIX + userId);
        log.info("Token deleted from Redis for userId={}", userId);
    }
}

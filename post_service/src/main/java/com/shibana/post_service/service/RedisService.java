package com.shibana.post_service.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class RedisService {
    RedisTemplate<String, Object> redisTemplate;

    public void addToSet(String key, Object value) {
        redisTemplate.opsForSet().add(key, value);
    }

    public void removeFromSet(String key, Object value) {
        redisTemplate.opsForSet().remove(key, value);
    }

    public boolean isMemberOfSet(String key, Object value) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
    }
}

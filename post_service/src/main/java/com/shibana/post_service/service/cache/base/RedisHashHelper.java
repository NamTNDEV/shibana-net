package com.shibana.post_service.service.cache.base;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisHashHelper {
    RedisTemplate<String, Object> redisTemplate;
    StringRedisTemplate stringRedisTemplate;

    public void hSet(String key, String field, Object value) {
        stringRedisTemplate.opsForHash().put(key, field, value);
    }

    public void hSet(String key, String field, Object value, Duration duration) {
        stringRedisTemplate.opsForHash().put(key, field, value);
        stringRedisTemplate.expire(key, duration);
    }

    public void hPutAll(String key, Map<String, Object> map) {
        stringRedisTemplate.opsForHash().putAll(key, map);
    }

    public void hPutAll(String key, Map<String, Object> map, Duration duration) {
        stringRedisTemplate.opsForHash().putAll(key, map);
        stringRedisTemplate.expire(key, duration);
    }

    public Object hGet(String key, String field) {
        return stringRedisTemplate.opsForHash().get(key, field);
    }

    public Map<Object, Object> hGetAll(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    public void hDelete(String key, String field) {
        stringRedisTemplate.opsForHash().delete(key, field);
    }

    public boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    public Long hIncrBy(String key, Object field, long delta) {
        return stringRedisTemplate.opsForHash().increment(key, field, delta);
    }
}

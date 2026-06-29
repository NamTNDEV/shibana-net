package com.shibana.post_service.service.cache.base;

import com.shibana.post_service.exception.AppException;
import com.shibana.post_service.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class RedisCacheTemplate {
    RedissonClient redissonClient;
    RedisHashHelper redisHashHelper;

    public void executeWithDoubleCheckLock(String redisKey, Runnable dbFetchAction, long lockTimeout) {
        if (redisHashHelper.hasKey(redisKey)) return;

        String redLockKey = "lock:warmup:" + redisKey;
        RLock rlock = redissonClient.getLock(redLockKey);
        boolean acquired = false;

        try {
            acquired = rlock.tryLock(lockTimeout, TimeUnit.MILLISECONDS);

            if (acquired) {
                if (!redisHashHelper.hasKey(redisKey)) {
                    dbFetchAction.run();
                }
            } else {
                log.warn("[executeWithDoubleCheckLock]::Hệ thống đang nghẽn, từ chối request warm-up cho key: {}", redisKey);
                throw new AppException(ErrorCode.SYSTEM_BUSY);
            }
        } catch (Exception e) {
            log.error("[executeWithDoubleCheckLock]::Luồng lấy khóa bị ngắt", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Tiến trình bị gián đoạn");
        } finally {
            if (acquired && rlock.isHeldByCurrentThread()) {
                rlock.unlock();
            }
        }
    }
}

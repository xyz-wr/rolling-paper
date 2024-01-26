package com.wrbread.roll.rollingpaper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void setValues(String key, String value) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value);
    }

    // 만료시간 설정 -> 자동 삭제
    public void setValuesWithTimeout(String key, String value, long duration) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuration);
    }

    public String getValues(String key){
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    @Transactional
    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }
}

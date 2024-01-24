package com.wrbread.roll.rollingpaper.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
public class RedisServiceTest {

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    @DisplayName("set value 테스트")
    public void testSetValues() {
        String key = "testKey";
        String value = "testValue";

        redisService.setValues(key, value);

        String retrievedValue = redisTemplate.opsForValue().get(key);
        assertEquals(value, retrievedValue);
    }

    @Test
    @DisplayName("SetValuesWithTimeout 테스트")
    public void testSetValuesWithTimeout() {
        String key = "testKeyTimeout";
        String value = "testValueTimeout";
        long timeout = 1000;

        redisService.setValuesWithTimeout(key, value, timeout);

        String retrievedValue = redisTemplate.opsForValue().get(key);
        assertEquals(value, retrievedValue);

        try {
            Thread.sleep(timeout + 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        retrievedValue = redisTemplate.opsForValue().get(key);
        assertNull(retrievedValue);
    }

    @Test
    @DisplayName("GetValues 테스트")
    public void testGetValues() {
        String key = "testKeyGet";
        String value = "testValueGet";

        redisTemplate.opsForValue().set(key, value);

        String retrievedValue = redisService.getValues(key);
        assertEquals(value, retrievedValue);
    }

    @Test
    @DisplayName("DeleteValues 테스트")
    public void testDeleteValues() {
        String key = "testKeyDelete";
        String value = "testValueDelete";

        redisTemplate.opsForValue().set(key, value);

        String retrievedValue = redisTemplate.opsForValue().get(key);
        assertEquals(value, retrievedValue);

        redisService.deleteValues(key);

        retrievedValue = redisTemplate.opsForValue().get(key);
        assertNull(retrievedValue);
    }
}
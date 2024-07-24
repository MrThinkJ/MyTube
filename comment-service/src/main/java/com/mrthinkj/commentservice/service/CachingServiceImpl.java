package com.mrthinkj.commentservice.service;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@AllArgsConstructor
public class CachingServiceImpl implements CachingService{
    RedisTemplate<String, Object> redisTemplate;
    @Override
    public Object getObjectFromKey(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void putObject(String key, Object object, Duration cacheTTL) {
        redisTemplate.opsForValue().set(key, object, cacheTTL);
    }
}

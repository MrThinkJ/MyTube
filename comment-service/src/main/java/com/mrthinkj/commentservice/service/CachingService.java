package com.mrthinkj.commentservice.service;

import java.time.Duration;

public interface CachingService {
    Object getObjectFromKey(String key);
    void putObject(String key, Object object, Duration cacheTTL);
}

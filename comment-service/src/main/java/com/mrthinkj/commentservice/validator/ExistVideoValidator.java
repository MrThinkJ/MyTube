package com.mrthinkj.commentservice.validator;

import com.mrthinkj.commentservice.annotation.ExistVideo;
import com.mrthinkj.core.exception.ServiceUnavailableException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static com.mrthinkj.core.utils.APIUtils.VIDEO_API;

public class ExistVideoValidator implements ConstraintValidator<ExistVideo, Long> {
    private final String VIDEO_ID_CACHE_REDIS_PREFIX = "videoId:";
    WebClient.Builder webclientBuilder;
    RedisTemplate<String, Object> redisTemplate;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    public ExistVideoValidator(WebClient.Builder webclientBuilder, RedisTemplate<String, Object> redisTemplate) {
        this.webclientBuilder = webclientBuilder;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void initialize(ExistVideo constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long videoId, ConstraintValidatorContext constraintValidatorContext) {
        Object videoIdObj = redisTemplate.opsForValue().get(VIDEO_ID_CACHE_REDIS_PREFIX+videoId);
        if (videoIdObj != null)
            return Boolean.TRUE.equals(videoIdObj);
        Boolean isExist = webclientBuilder.build().get()
                .uri(VIDEO_API+"/check/"+videoId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ex ->{
                    throw new RuntimeException("Error with client");
                })
                .bodyToMono(Boolean.class)
                .timeout(Duration.ofSeconds(10))
                .onErrorMap(TimeoutException.class, ex ->{
                    throw new ServiceUnavailableException("Video service is unavailable");
                })
                .doOnError(ex ->{
                    LOGGER.error("Exception occurred: {}", ex.getMessage());
                })
                .block();
        if (isExist == null)
            return false;
        redisTemplate.opsForValue().set(VIDEO_ID_CACHE_REDIS_PREFIX+videoId, isExist);
        return isExist;
    }
}

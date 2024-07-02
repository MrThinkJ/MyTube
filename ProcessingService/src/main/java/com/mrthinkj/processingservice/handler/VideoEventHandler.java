package com.mrthinkj.processingservice.handler;

import com.mrthinkj.core.entity.VideoEvent;
import com.mrthinkj.processingservice.service.ProcessVideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "video-events-topic")
public class VideoEventHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    ProcessVideoService processVideoService;
    @KafkaHandler
    public void handleVideoUploadEvent(
            @Payload VideoEvent videoEvent,
            @Header("idempotencyKey") String idempotencyKey
            ){
        LOGGER.info("Receive new video event with video UUID: {}", videoEvent.getVideoId());
        if (processVideoService.checkIfExistIdempotencyKey(idempotencyKey)){
            LOGGER.info("Found duplicate idempotency key: {}", idempotencyKey);
            return;
        }

        processVideoService.processVideo(videoEvent.getVideoId());
    }
}

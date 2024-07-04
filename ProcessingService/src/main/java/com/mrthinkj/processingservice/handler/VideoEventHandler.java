package com.mrthinkj.processingservice.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrthinkj.core.entity.VideoEvent;
import com.mrthinkj.processingservice.service.ProcessVideoService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@KafkaListener(topics = "video-events-topic")
public class VideoEventHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    ProcessVideoService processVideoService;
    @KafkaHandler
    @Transactional(rollbackOn = Exception.class)
    public void handle(
            @Payload String payload,
            @Header("idempotencyKey") String idempotencyKey
            ){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            VideoEvent videoEvent = objectMapper.readValue(payload, VideoEvent.class);
            LOGGER.info("Receive new video event with video UUID: {}", videoEvent.getVideoId());
            if (processVideoService.checkIfExistIdempotencyKey(idempotencyKey)){
                LOGGER.info("Found duplicate idempotency key: {}", idempotencyKey);
                return;
            }
            LOGGER.info("Process video");
            processVideoService.processVideo(videoEvent.getVideoId()+".mp4");
        } catch (JsonProcessingException e) {
            LOGGER.error("Error deserializing payload", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

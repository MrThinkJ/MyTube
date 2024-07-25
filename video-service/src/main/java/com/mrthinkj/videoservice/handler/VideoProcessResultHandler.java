package com.mrthinkj.videoservice.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrthinkj.core.entity.VideoEvent;
import com.mrthinkj.core.entity.VideoState;
import com.mrthinkj.videoservice.service.VideoService;
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
@KafkaListener(topics = "video-result-events-topic")
public class VideoProcessResultHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    VideoService videoService;
    @KafkaHandler
    @Transactional(rollbackOn = Exception.class)
    public void handleProcessingResult(
            @Payload String payload,
            @Header("videoUUID") String videoUUID
    ){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            VideoState videoState = objectMapper.readValue(payload, VideoState.class);
            LOGGER.info("Receive new video processing result event with video UUID: {}", videoUUID);
            LOGGER.info("Update video state");
            videoService.setStateForVideo(videoUUID, videoState);
            videoService.sendNewVideoNotificationToSubscribers(videoUUID);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error deserializing payload", e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

package com.mrthinkj.searchservice.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrthinkj.core.entity.VideoUpdateEvent;
import com.mrthinkj.searchservice.entity.VideoDocument;
import com.mrthinkj.searchservice.service.VideoSearchService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@KafkaListener(topics = "video-index-events-topic")
public class VideoUpdateHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    VideoSearchService videoSearchService;

    @KafkaHandler
    public void handleVideoUpdateEvent(
            @Payload VideoUpdateEvent videoUpdateEvent
    ){
        LOGGER.info("Receive new event with id: {}", videoUpdateEvent.getId());
        VideoDocument videoDocument = convertFromEventToVideoDocument(videoUpdateEvent);
        switch (videoUpdateEvent.getVideoUpdateOperation()){
            case CREATE -> {
                videoSearchService.indexVideo(videoDocument);
            }
            case UPDATE -> {
                videoSearchService.updateVideo(videoDocument);
            }
            case DELETE -> {
                videoSearchService.deleteVideo(videoDocument);
            }
        }
    }

    private VideoDocument convertFromEventToVideoDocument(VideoUpdateEvent videoUpdateEvent){
        VideoDocument videoDocument = new VideoDocument();
        videoDocument.setId(videoUpdateEvent.getId());
        videoDocument.setVideoUUID(videoUpdateEvent.getVideoUUID());
        videoDocument.setTitle(videoUpdateEvent.getTitle());
        videoDocument.setThumbnailUUID(videoUpdateEvent.getThumbnailUUID());
        videoDocument.setPosterId(videoUpdateEvent.getPosterId());
        videoDocument.setPublishDate(videoUpdateEvent.getPublishDate());
        return null;
    }
}

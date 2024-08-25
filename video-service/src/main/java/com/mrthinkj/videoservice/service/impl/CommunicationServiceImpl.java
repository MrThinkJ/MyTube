package com.mrthinkj.videoservice.service.impl;

import com.mrthinkj.core.entity.*;
import com.mrthinkj.videoservice.entity.Video;
import com.mrthinkj.videoservice.repository.VideoRepository;
import com.mrthinkj.videoservice.service.CommunicationService;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CommunicationServiceImpl implements CommunicationService {
    VideoRepository videoRepository;
    KafkaTemplate<String, NotificationEvent> notificationEventKafkaTemplate;
    KafkaTemplate<String, VideoUpdateEvent> videoUpdateEventKafkaTemplate;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public void sendNewVideoNotificationToSubscribers(String videoUUID) {
        Video video = videoRepository.findByVideoUUID(videoUUID).orElseThrow(
                ()-> new RuntimeException(String.format("Video with UUID: %s does not exist", videoUUID)));
        NewVideoNotification newVideoNotification = NewVideoNotification.builder()
                .videoUUID(videoUUID)
                .videoTitle(video.getTitle())
                .videoOwnerId(video.getPosterId())
                .build();
        String eventId = UUID.randomUUID().toString();
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .id(eventId)
                .notification(newVideoNotification)
                .type(NotificationType.NEW_VIDEO)
                .build();

        ProducerRecord<String, NotificationEvent> record = new ProducerRecord<>(
                "notification-events-topic", eventId, notificationEvent
        );

        try{
            SendResult<String, NotificationEvent> result = notificationEventKafkaTemplate.send(record)
                    .get();
            LOGGER.info("Send message successfully to topic {}", "notification-events-topic");
            LOGGER.info("Partition: "+result.getRecordMetadata().partition());
            LOGGER.info("Topic: "+result.getRecordMetadata().topic());
            LOGGER.info("Offset: "+result.getRecordMetadata().offset());
            LOGGER.info("VideoUUID: {}", videoUUID);
        } catch (Exception e){
            LOGGER.error("Error when send message to topic {}: {}", "notification-events-topic", e.getMessage());
        }
    }

    @Override
    public void sendNewVideoInfoForSearchEngine(String videoUUID) {
        Video video = videoRepository.findByVideoUUID(videoUUID).orElseThrow(
                ()-> new RuntimeException(String.format("Video with UUID: %s does not exist", videoUUID)));
        VideoUpdateEvent videoUpdateEvent = VideoUpdateEvent.builder()
                .videoUUID(videoUUID)
                .id(video.getId())
                .posterId(video.getPosterId())
                .title(video.getTitle())
                .publishDate(video.getPublishDate())
                .thumbnailUUID(video.getThumbnailUUID())
                .videoUpdateOperation(VideoUpdateOperation.CREATE)
                .build();
        String eventId = UUID.randomUUID().toString();
        ProducerRecord<String, VideoUpdateEvent> record = new ProducerRecord<>(
                "video-index-events-topic", eventId, videoUpdateEvent
        );
        try {
            SendResult<String, VideoUpdateEvent> result = videoUpdateEventKafkaTemplate.send(record).get();
            LOGGER.info("Send message successfully to topic {}", "video-index-events-topic");
            LOGGER.info("Partition: "+result.getRecordMetadata().partition());
            LOGGER.info("Topic: "+result.getRecordMetadata().topic());
            LOGGER.info("Offset: "+result.getRecordMetadata().offset());
        } catch (Exception e){
            LOGGER.error("Exception when send message to topic {}: {}", "video-index-events-topic", e.getMessage());
        }
    }
}

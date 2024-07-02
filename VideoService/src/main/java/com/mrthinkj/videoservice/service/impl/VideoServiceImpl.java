package com.mrthinkj.videoservice.service.impl;

import com.mrthinkj.core.entity.VideoEvent;
import com.mrthinkj.core.entity.VideoState;
import com.mrthinkj.videoservice.config.StorageConfiguration;
import com.mrthinkj.videoservice.entity.Video;
import com.mrthinkj.videoservice.payload.VideoDTO;
import com.mrthinkj.videoservice.payload.VideoUploadDTO;
import com.mrthinkj.videoservice.repository.VideoRepository;
import com.mrthinkj.videoservice.service.MinioService;
import com.mrthinkj.videoservice.service.VideoService;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

@Service
@AllArgsConstructor
public class VideoServiceImpl implements VideoService {
    KafkaTemplate<String, VideoEvent> kafkaTemplate;
    MinioService minioService;
    StorageConfiguration storageConfiguration;
    VideoRepository videoRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @Override
    public VideoDTO upload(VideoUploadDTO videoUploadDTO) throws Exception {
        String eventId = UUID.randomUUID().toString();
        String videoUUID = UUID.randomUUID().toString();
        String idempotencyKey = UUID.randomUUID().toString();
        MultipartFile videoContent = videoUploadDTO.getVideoContent();
        minioService.putObject(videoContent, videoUUID);

        // Save Video object to database
        Video video = new Video();
        video.setVideoName(videoUploadDTO.getVideoName());
        video.setVideoUUID(videoUUID);
        video.setState(VideoState.PROCESS);
        video.setTitle(videoUploadDTO.getTitle());
        video.setPublishDate(LocalDate.now());
        video.setPosterId(videoUploadDTO.getPosterId());
        video.setViewCount(0);
        videoRepository.save(video);

        // Create VideoEventObject
        VideoEvent videoEvent = VideoEvent.builder()
                .id(eventId)
                .videoId(videoUUID)
                .ownerId(videoUploadDTO.getPosterId())
                .build();
        ProducerRecord<String, VideoEvent> record = new ProducerRecord<>(
                "video-events-topic", eventId, videoEvent
        );

        // Send VideoEvent to topic video-events-topic
        record.headers().add("idempotencyKey", idempotencyKey.getBytes());
        SendResult<String, VideoEvent> result = kafkaTemplate.send(record).get();
        LOGGER.info("Send message successfully to kafka broker");
        LOGGER.info("Partition: "+result.getRecordMetadata().partition());
        LOGGER.info("Topic: "+result.getRecordMetadata().topic());
        LOGGER.info("Offset: "+result.getRecordMetadata().offset());
        LOGGER.info("VideoUUID: {}", videoUUID);

        // Return VideoDTO
        return VideoDTO.builder()
                .videoUUID(videoUUID)
                .videoName(videoUploadDTO.getVideoName())
                .posterId(videoUploadDTO.getPosterId())
                .publishDate(LocalDate.now())
                .title(videoUploadDTO.getTitle())
                .build();
    }

    @Override
    public void delete(Long videoId) {

    }
}

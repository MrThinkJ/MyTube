package com.mrthinkj.videoservice.service.impl;

import com.mrthinkj.core.entity.*;
import com.mrthinkj.videoservice.config.StorageConfiguration;
import com.mrthinkj.videoservice.config.StreamConfiguration;
import com.mrthinkj.videoservice.entity.Video;
import com.mrthinkj.core.exception.DoesNotExistException;
import com.mrthinkj.videoservice.exception.VideoStateNotSuccessException;
import com.mrthinkj.videoservice.payload.PagedVideosResponse;
import com.mrthinkj.videoservice.payload.VideoDTO;
import com.mrthinkj.videoservice.payload.VideoUploadDTO;
import com.mrthinkj.videoservice.repository.VideoRepository;
import com.mrthinkj.videoservice.service.MinioService;
import com.mrthinkj.videoservice.service.VideoService;
import lombok.AllArgsConstructor;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class VideoServiceImpl implements VideoService {
    KafkaTemplate<String, VideoEvent> kafkaTemplate;
    MinioService minioService;
    StorageConfiguration storageConfiguration;
    StreamConfiguration streamConfiguration;
    VideoRepository videoRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public PagedVideosResponse getPageOfVideo(int page, int size) {
        Page<Video> videoPage = videoRepository.findAll(PageRequest.of(page, size));
        return PagedVideosResponse.builder()
                .content(videoPage.getContent().stream().map(this::mapToDTO).collect(Collectors.toList()))
                .page(videoPage.getNumber())
                .size(videoPage.getSize())
                .totalPage(videoPage.getTotalPages())
                .isLastPage(videoPage.isLast())
                .build();
    }

    @Override
    public VideoDTO upload(VideoUploadDTO videoUploadDTO) throws Exception {
        String eventId = UUID.randomUUID().toString();
        String videoUUID = UUID.randomUUID().toString();
        String idempotencyKey = UUID.randomUUID().toString();
        MultipartFile videoContent = videoUploadDTO.getVideoContent();
        MultipartFile videoThumbnail = videoUploadDTO.getThumbnail();
        minioService.putObject(videoContent, videoUUID + "/" + videoUUID + ".mp4");
        minioService.putObject(videoThumbnail, videoUUID + "/" + videoUUID + "." + FileNameUtils.getExtension(videoThumbnail.getOriginalFilename()));

        // Save Video object to database
        Video video = new Video();
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
        LOGGER.info("Partition: " + result.getRecordMetadata().partition());
        LOGGER.info("Topic: " + result.getRecordMetadata().topic());
        LOGGER.info("Offset: " + result.getRecordMetadata().offset());
        LOGGER.info("VideoUUID: {}", videoUUID);

        // Return VideoDTO
        return mapToDTO(video);
    }

    @Override
    @Transactional(readOnly = true)
    public StreamingResponseBody getM3U8(String videoUUID) throws Exception {
        Video video = videoRepository.findByVideoUUID(videoUUID).orElseThrow(
                () -> new DoesNotExistException("This video UUID does not exist"));
        if (video.getState() != VideoState.SUCCESS)
            throw new VideoStateNotSuccessException("This video is processing or process failed");

        InputStream inputStream = minioService.getObject(storageConfiguration.getBucketStream(), videoUUID + "/index.m3u8");
        return outputStream -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)); outputStream) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.endsWith(".ts")) {
                        outputStream.write((streamConfiguration.getStreamPrefix() + videoUUID + "/" + line).getBytes());
                        outputStream.write(System.lineSeparator().getBytes());
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error when read the m3u8 file");
            }
        };
    }

    @Override
    @Transactional(readOnly = true)
    public StreamingResponseBody getTs(String videoUUID, String tsFile) {
        Video video = videoRepository.findByVideoUUID(videoUUID).orElseThrow(
                () -> new DoesNotExistException("This video UUID does not exist"));
        if (video.getState() != VideoState.SUCCESS)
            throw new VideoStateNotSuccessException("This video is processing or process failed");

        String tsFilePath = videoUUID + "/" + tsFile + ".ts";
        LOGGER.info(tsFilePath);
        return outputStream -> {
            try (InputStream inputStream = minioService.getObject(storageConfiguration.getBucketStream(), tsFilePath); outputStream) {
                byte[] buffer = new byte[2048];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            } catch (Exception e) {
                LOGGER.error("Error when reading ts file");
            }
        };
    }

    @Override
    public void setStateForVideo(String videoUUID, VideoState stateForVideo) {
        Video video = videoRepository.findByVideoUUID(videoUUID).orElseThrow(
                () -> new RuntimeException(String.format("Video with UUID: %s does not exist", videoUUID)));
        video.setState(stateForVideo);
        videoRepository.save(video);
    }

    @Override
    public boolean isVideoIdExist(Long videoId) {
        Video video = videoRepository.findById(videoId).orElse(null);
        return video != null;
    }

    @Override
    public void delete(Long videoId) {

    }

    private VideoDTO mapToDTO(Video video) {
        return VideoDTO.builder()
                .id(video.getId())
                .videoUUID(video.getVideoUUID())
                .posterId(video.getPosterId())
                .publishDate(LocalDate.now())
                .title(video.getTitle())
                .build();
    }
}

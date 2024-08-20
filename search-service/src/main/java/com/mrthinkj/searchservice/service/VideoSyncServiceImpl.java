package com.mrthinkj.searchservice.service;

import com.mrthinkj.core.exception.ServiceUnavailableException;
import com.mrthinkj.searchservice.entity.VideoDocument;
import com.mrthinkj.searchservice.payload.PagedVideosResponse;
import com.mrthinkj.searchservice.payload.VideoDTO;
import com.mrthinkj.searchservice.repository.VideoSearchRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static com.mrthinkj.core.utils.APIUtils.VIDEO_API;

@Service
@AllArgsConstructor
public class VideoSyncServiceImpl implements VideoSyncService{
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    ElasticsearchOperations elasticsearchOperations;
    VideoSearchRepository videoSearchRepository;
    WebClient.Builder webClientBuilder;
    @Override
    public void initialSync() {
        int page = 0;
        int size = 1000;
        boolean hasMore = true;
        while(hasMore){
            PagedVideosResponse response = fetchPagedVideos(page, size);
            if (response == null){
                LOGGER.error("Null response when fetch video for sync");
                throw new RuntimeException("Null Response");
            }
            List<VideoDTO> videos = response.getContent();
            List<VideoDocument> videoDocuments = videos.stream().map(this::convertToSearchDocument).collect(Collectors.toList());
            elasticsearchOperations.save(videoDocuments);
            hasMore = !response.isLastPage();
            page++;
            LOGGER.info("Synced page {} of videos", page);
        }
    }

    @Override
    @Scheduled(cron = "0 0 2 * * ?")
    public void consistencyCheck() {
        int page = 0;
        int size = 100;
        PagedVideosResponse response = fetchPagedVideos(page, size);
        int lastPage = response.getTotalPage()-1;
        PagedVideosResponse lastPageResponse = fetchPagedVideos(lastPage, size);
        List<VideoDTO> lastPageVideos = lastPageResponse.getContent();
        for (VideoDTO videoDTO : lastPageVideos){
            videoSearchRepository.findById(videoDTO.getId()).ifPresentOrElse(x -> {
                if (!checkForConsistency(x, videoDTO))
                    elasticsearchOperations.save(convertToSearchDocument(videoDTO));
            }, this::initialSync);
        }
    }

    private VideoDocument convertToSearchDocument(VideoDTO videoDTO){
        VideoDocument videoDocument = new VideoDocument();
        videoDocument.setId(videoDTO.getId());
        videoDocument.setTitle(videoDTO.getTitle());
        videoDocument.setPublishDate(videoDTO.getPublishDate());
        videoDocument.setVideoUUID(videoDTO.getVideoUUID());
        videoDocument.setThumbnailUUID(videoDTO.getThumbnailUUID());
        videoDocument.setPosterId(videoDTO.getPosterId());
        return videoDocument;
    }

    private PagedVideosResponse fetchPagedVideos(int page, int size){
        return webClientBuilder.build()
                .get()
                .uri(VIDEO_API+ String.format("?page=%s&size=%s", page, size))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    LOGGER.error("Client error: {}", clientResponse.statusCode());
                    throw new RuntimeException("Error with client");
                })
                .bodyToMono(PagedVideosResponse.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorMap(TimeoutException.class, ex -> {
                    throw new ServiceUnavailableException("User service unavailable");
                })
                .doOnError(ex ->{
                    LOGGER.error("Exception throw: {}", ex.getMessage());
                })
                .block();
    }

    private boolean checkForConsistency(VideoDocument videoDocument, VideoDTO videoDTO){
        if (videoDTO == null || videoDocument == null) {
            return false;
        }

        // Compare each field
        boolean idMatch = Objects.equals(videoDTO.getId(), videoDocument.getId());
        boolean videoUUIDMatch = Objects.equals(videoDTO.getVideoUUID(), videoDocument.getVideoUUID());
        boolean thumbnailUUIDMatch = Objects.equals(videoDTO.getThumbnailUUID(), videoDocument.getThumbnailUUID());
        boolean posterIdMatch = Objects.equals(videoDTO.getPosterId(), videoDocument.getPosterId());
        boolean titleMatch = Objects.equals(videoDTO.getTitle(), videoDocument.getTitle());
        boolean publishDateMatch = Objects.equals(videoDTO.getPublishDate(), videoDocument.getPublishDate());

        return idMatch && videoUUIDMatch && thumbnailUUIDMatch && posterIdMatch && titleMatch && publishDateMatch;
    }
}

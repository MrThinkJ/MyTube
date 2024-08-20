package com.mrthinkj.searchservice.service;

import com.mrthinkj.searchservice.entity.VideoDocument;
import com.mrthinkj.searchservice.repository.VideoSearchRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.util.List;

public interface VideoSearchService {
    List<VideoDocument> searchVideos(String keyword, int page, int size);
    void indexVideo(VideoDocument videoDocument);
    void updateVideo(VideoDocument videoDocument);
    void deleteVideo(VideoDocument videoDocument);
}

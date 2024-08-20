package com.mrthinkj.searchservice.service;

import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import com.mrthinkj.searchservice.entity.VideoDocument;
import com.mrthinkj.searchservice.repository.VideoSearchRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.Queries;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class VideoSearchServiceImpl implements VideoSearchService{
    ElasticsearchOperations elasticsearchOperations;
    VideoSearchRepository videoSearchRepository;

    @Override
    public List<VideoDocument> searchVideos(String keyword, int page, int size) {
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(Queries.matchQueryAsQuery("title", keyword, Operator.Or, 1.0f))
                .withPageable(PageRequest.of(page, size))
                .build();
        SearchHits<VideoDocument> searchHits = elasticsearchOperations.search(nativeQuery, VideoDocument.class);
        return searchHits.get().map(SearchHit::getContent).collect(Collectors.toList());
    }

    @Override
    public void indexVideo(VideoDocument videoDocument) {
        elasticsearchOperations.save(videoDocument);
    }

    @Override
    public void updateVideo(VideoDocument videoDocument) {
        elasticsearchOperations.save(videoDocument);
    }

    @Override
    public void deleteVideo(VideoDocument videoDocument) {
        elasticsearchOperations.delete(videoDocument);
    }
}

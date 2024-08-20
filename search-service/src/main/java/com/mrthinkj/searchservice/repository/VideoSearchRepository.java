package com.mrthinkj.searchservice.repository;

import com.mrthinkj.searchservice.entity.VideoDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoSearchRepository extends ElasticsearchRepository<VideoDocument, Long> {
}

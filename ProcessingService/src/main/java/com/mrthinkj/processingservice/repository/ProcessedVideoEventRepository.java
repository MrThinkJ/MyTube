package com.mrthinkj.processingservice.repository;

import com.mrthinkj.processingservice.entity.ProcessedVideoEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedVideoEventRepository extends JpaRepository<ProcessedVideoEvent, Long> {
    ProcessedVideoEvent findByIdempotencyKey(String idempotencyKey);
}

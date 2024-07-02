package com.mrthinkj.processingservice.service;

import com.mrthinkj.processingservice.entity.ProcessedVideoEvent;
import com.mrthinkj.processingservice.repository.ProcessedVideoEventRepository;
import org.springframework.stereotype.Service;

@Service
public class ProcessVideoServiceImpl implements ProcessVideoService {
    ProcessedVideoEventRepository processedVideoEventRepository;

    @Override
    public boolean checkIfExistIdempotencyKey(String idempotencyKey) {
        ProcessedVideoEvent processedVideoEvent = processedVideoEventRepository.findByIdempotencyKey(idempotencyKey);
        return processedVideoEvent != null;
    }

    @Override
    public void processVideo(String videoUUID) {

    }
}

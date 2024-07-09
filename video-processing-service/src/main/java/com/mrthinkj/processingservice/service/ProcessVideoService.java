package com.mrthinkj.processingservice.service;

public interface ProcessVideoService {
    boolean checkIfExistIdempotencyKey(String idempotencyKey);
    void processVideo(String videoUUID) throws Exception;
}

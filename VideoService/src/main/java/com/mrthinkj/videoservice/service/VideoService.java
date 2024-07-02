package com.mrthinkj.videoservice.service;

import com.mrthinkj.videoservice.payload.VideoDTO;
import com.mrthinkj.videoservice.payload.VideoUploadDTO;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface VideoService {
    VideoDTO upload(VideoUploadDTO videoUploadDTO) throws Exception;
    void delete(Long videoId);
}

package com.mrthinkj.videoservice.service;

import com.mrthinkj.core.entity.VideoState;
import com.mrthinkj.videoservice.payload.PagedVideosResponse;
import com.mrthinkj.videoservice.payload.VideoDTO;
import com.mrthinkj.videoservice.payload.VideoUploadDTO;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public interface VideoService {
    PagedVideosResponse getPageOfVideo(int page, int size);
    VideoDTO upload(VideoUploadDTO videoUploadDTO) throws Exception;
    StreamingResponseBody getM3U8(String videoUUID) throws Exception;
    StreamingResponseBody getTs(String videoUUID, String tsFile) throws Exception;
    void setStateForVideo(String videoUUID,VideoState stateForVideo);
    boolean isVideoIdExist(Long videoId);
    void delete(Long videoId);
}

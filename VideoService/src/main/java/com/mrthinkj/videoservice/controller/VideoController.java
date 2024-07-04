package com.mrthinkj.videoservice.controller;

import com.mrthinkj.videoservice.payload.VideoDTO;
import com.mrthinkj.videoservice.payload.VideoUploadDTO;
import com.mrthinkj.videoservice.service.VideoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/videos")
public class VideoController {
    VideoService videoService;

    @PostMapping("/upload")
    public ResponseEntity<VideoDTO> upload(@ModelAttribute VideoUploadDTO videoUploadDTO) throws Exception {
        VideoDTO videoDTO = videoService.upload(videoUploadDTO);
        return new ResponseEntity<>(videoDTO, HttpStatus.CREATED);
    }
}

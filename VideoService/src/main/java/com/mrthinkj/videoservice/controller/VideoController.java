package com.mrthinkj.videoservice.controller;

import com.mrthinkj.videoservice.entity.TsRequest;
import com.mrthinkj.videoservice.entity.VideoRequest;
import com.mrthinkj.videoservice.payload.VideoDTO;
import com.mrthinkj.videoservice.payload.VideoUploadDTO;
import com.mrthinkj.videoservice.service.VideoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/videos")
public class VideoController {
    VideoService videoService;
    @PostMapping("/upload")
    public ResponseEntity<VideoDTO> upload(@ModelAttribute @Valid VideoUploadDTO videoUploadDTO) throws Exception {
        VideoDTO videoDTO = videoService.upload(videoUploadDTO);
        return new ResponseEntity<>(videoDTO, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{videoUUID}/index.m3u8", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> getM3U8(@Valid VideoRequest videoRequest) throws Exception {
        return ResponseEntity.ok(videoService.getM3U8(videoRequest.getVideoUUID()));
    }

    @GetMapping(value = "/{videoUUID}/{tsFile:.+}.ts")
    public ResponseEntity<StreamingResponseBody> getTs(@Valid TsRequest tsRequest) throws Exception {
        return ResponseEntity.ok(videoService.getTs(tsRequest.getVideoUUID(), tsRequest.getTsFile()));
    }

}

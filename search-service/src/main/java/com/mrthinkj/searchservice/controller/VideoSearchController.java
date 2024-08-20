package com.mrthinkj.searchservice.controller;

import com.mrthinkj.searchservice.entity.VideoDocument;
import com.mrthinkj.searchservice.service.VideoSearchService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/search/videos")
public class VideoSearchController {
    VideoSearchService videoSearchService;

    @GetMapping
    public ResponseEntity<List<VideoDocument>> searchVideo(@RequestParam String keyword,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(videoSearchService.searchVideos(keyword, page, size));
    }
}

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

import static com.mrthinkj.core.utils.WebUtils.DEFAULT_PAGE_NUM;
import static com.mrthinkj.core.utils.WebUtils.DEFAULT_PAGE_SIZE;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/search")
public class VideoSearchController {
    VideoSearchService videoSearchService;

    @GetMapping("/videos")
    public ResponseEntity<List<VideoDocument>> searchVideo(@RequestParam String keyword,
                                                           @RequestParam(defaultValue = DEFAULT_PAGE_NUM) int page,
                                                           @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size){
        return ResponseEntity.ok(videoSearchService.searchVideos(keyword, page, size));
    }
}

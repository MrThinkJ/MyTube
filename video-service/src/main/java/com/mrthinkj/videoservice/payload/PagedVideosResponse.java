package com.mrthinkj.videoservice.payload;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagedVideosResponse {
    private List<VideoDTO> content;
    private int page;
    private int size;
    private int totalPage;
    private boolean isLastPage;
}

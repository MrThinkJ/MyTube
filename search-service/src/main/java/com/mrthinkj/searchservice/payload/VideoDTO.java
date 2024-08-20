package com.mrthinkj.searchservice.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoDTO {
    private Long id;
    private String videoUUID;
    private String thumbnailUUID;
    private Long posterId;
    private String title;
    private LocalDate publishDate;
}


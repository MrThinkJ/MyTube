package com.mrthinkj.videoservice.payload;

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
    private String videoUUID;
    private Long posterId;
    private String title;
    private LocalDate publishDate;
}


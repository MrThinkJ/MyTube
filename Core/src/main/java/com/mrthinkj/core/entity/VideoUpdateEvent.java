package com.mrthinkj.core.entity;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoUpdateEvent {
    private Long id;
    private String videoUUID;
    private String thumbnailUUID;
    private String title;
    private LocalDate publishDate;
    private Long posterId;
    private VideoUpdateOperation videoUpdateOperation;
}

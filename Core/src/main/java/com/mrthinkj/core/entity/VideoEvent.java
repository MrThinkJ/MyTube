package com.mrthinkj.core.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoEvent {
    private String id;
    private String videoId;
    private Long ownerId;
}

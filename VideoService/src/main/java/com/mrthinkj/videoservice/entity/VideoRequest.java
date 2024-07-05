package com.mrthinkj.videoservice.entity;

import com.mrthinkj.videoservice.annotation.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoRequest {
    @UUID
    String videoUUID;
}

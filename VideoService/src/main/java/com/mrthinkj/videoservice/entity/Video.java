package com.mrthinkj.videoservice.entity;

import com.mrthinkj.core.entity.VideoState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "video")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String videoUUID;
    private String thumbnailUUID;
    private Long posterId;
    private String title;
    private Integer viewCount;
    private LocalDate publishDate;
    private VideoState state;
}

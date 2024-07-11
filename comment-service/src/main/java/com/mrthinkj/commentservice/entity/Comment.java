package com.mrthinkj.commentservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // TODO: Valid videoId
    private Long videoId;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long parentId;
    private int likesCount;
    private int dislikesCount;
    private int repliesCount;
}

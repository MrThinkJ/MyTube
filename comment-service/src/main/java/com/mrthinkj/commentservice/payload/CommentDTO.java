package com.mrthinkj.commentservice.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDTO {
    private Long id;
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

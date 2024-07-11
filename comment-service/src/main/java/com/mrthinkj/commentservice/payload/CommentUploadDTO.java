package com.mrthinkj.commentservice.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentUploadDTO {
    private Long videoId;
    private String content;
    private Long parentId;
    private int likesCount;
    private int dislikesCount;
    private int repliesCount;
}

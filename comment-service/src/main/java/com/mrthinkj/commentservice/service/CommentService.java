package com.mrthinkj.commentservice.service;

import com.mrthinkj.commentservice.entity.Comment;
import com.mrthinkj.commentservice.payload.CommentDTO;
import com.mrthinkj.commentservice.payload.CommentUploadDTO;

import java.util.List;

public interface CommentService {
    CommentDTO getCommentById(Long commentId);
    List<CommentDTO> getCommentByVideoId(Long videoId);
    List<CommentDTO> getCommentByParentId(Long parentId);
    CommentDTO createNewComment(String username, CommentUploadDTO comment);
    CommentDTO updateCommentById(Long commentId, String username, CommentUploadDTO comment);
    void deleteCommentById(Long commentId);
}

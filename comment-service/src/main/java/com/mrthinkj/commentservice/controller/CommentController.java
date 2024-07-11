package com.mrthinkj.commentservice.controller;

import com.mrthinkj.commentservice.payload.CommentDTO;
import com.mrthinkj.commentservice.payload.CommentUploadDTO;
import com.mrthinkj.commentservice.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {
    CommentService commentService;

    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Long id){
        return ResponseEntity.ok(commentService.getCommentById(id));
    }

    @GetMapping("/video/{id}")
    public ResponseEntity<List<CommentDTO>> getCommentByVideoId(@PathVariable Long id){
        return ResponseEntity.ok(commentService.getCommentByVideoId(id));
    }

    @GetMapping("/{id}/replies")
    public ResponseEntity<List<CommentDTO>> getCommentByParentId(@PathVariable Long id){
        return ResponseEntity.ok(commentService.getCommentByParentId(id));
    }

    @PostMapping
    public ResponseEntity<CommentDTO> createNewComment(@RequestBody CommentUploadDTO commentUploadDTO,
                                                       @RequestHeader("username") String username){
        return new ResponseEntity<>(commentService.createNewComment(username, commentUploadDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentDTO> updateCommentById(@PathVariable Long id,
                                                        @RequestBody CommentUploadDTO commentUploadDTO,
                                                        @RequestHeader("username") String username){
        return ResponseEntity.ok(commentService.updateCommentById(id, username, commentUploadDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCommentById(@PathVariable Long id){
        commentService.deleteCommentById(id);
        return ResponseEntity.ok("Delete successfully");
    }
}

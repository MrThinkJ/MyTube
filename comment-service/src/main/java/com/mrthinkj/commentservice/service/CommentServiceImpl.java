package com.mrthinkj.commentservice.service;

import com.mrthinkj.commentservice.entity.Comment;
import com.mrthinkj.commentservice.payload.CommentDTO;
import com.mrthinkj.commentservice.payload.CommentUploadDTO;
import com.mrthinkj.commentservice.repository.CommentRepository;
import com.mrthinkj.core.exception.DoesNotExistException;
import com.mrthinkj.core.exception.ServiceUnavailableException;
import com.mrthinkj.core.exception.UnauthorizedException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService{
    CommentRepository commentRepository;
    WebClient.Builder webClientBuilder;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Override
    public CommentDTO getCommentById(Long commentId) {
        return mapToDTO(commentRepository.findById(commentId).orElseThrow(
                () -> new DoesNotExistException(String.format("Comment with id %s does not exist", commentId))
        ));
    }

    @Override
    public List<CommentDTO> getCommentByVideoId(Long videoId) {
        return commentRepository.findByVideoId(videoId).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<CommentDTO> getCommentByParentId(Long parentId) {
        return commentRepository.findByParentId(parentId).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public CommentDTO createNewComment(String username, CommentUploadDTO commentDTO) {
        Long userId = fetchUserIdByUsername(username);
        Comment comment = mapToEntity(commentDTO);
        comment.setUserId(userId);
        return mapToDTO(commentRepository.save(comment));
    }

    @Override
    public CommentDTO updateCommentById(Long commentId, String username, CommentUploadDTO commentDTO) {
        Long userId = fetchUserIdByUsername(username);
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new DoesNotExistException(String.format("Comment with id %s does not exist", commentId))
        );
        if (!userId.equals(comment.getUserId()))
            throw new UnauthorizedException("Update not allowed for this user");
        comment.setContent(commentDTO.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        return mapToDTO(commentRepository.save(comment));
    }

    @Override
    public void deleteCommentById(Long commentId) {
        // TODO: Check for user id
        commentRepository.deleteById(commentId);
    }

    private CommentDTO mapToDTO(Comment comment){
        return CommentDTO.builder()
                .id(comment.getId())
                .videoId(comment.getVideoId())
                .userId(comment.getUserId())
                .parentId(comment.getParentId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .likesCount(comment.getLikesCount())
                .dislikesCount(comment.getDislikesCount())
                .repliesCount(comment.getRepliesCount())
                .build();
    }

    private Comment mapToEntity(CommentUploadDTO commentDTO){
        Comment comment = new Comment();
        comment.setVideoId(commentDTO.getVideoId());
        comment.setParentId(commentDTO.getParentId());
        comment.setContent(commentDTO.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        comment.setLikesCount(commentDTO.getLikesCount());
        comment.setDislikesCount(commentDTO.getDislikesCount());
        comment.setRepliesCount(commentDTO.getRepliesCount());
        return comment;
    }

    private Long fetchUserIdByUsername(String username) {
        return webClientBuilder.build()
                .get()
                .uri("http://user-service/api/v1/users/username/" + username)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    log.error("Client error: {}", clientResponse.statusCode());
                    throw new RuntimeException("Error with client");
                })
                .bodyToMono(Long.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorMap(TimeoutException.class, ex -> {
                    throw new ServiceUnavailableException("User service unavailable");
                })
                .doOnError(ex ->{
                    log.error("Exception throw: {}", ex.getMessage());
                })
                .block();
    }
}

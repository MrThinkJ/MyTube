package com.mrthinkj.userservice.controller;

import com.mrthinkj.userservice.payload.SubscriptionDTO;
import com.mrthinkj.userservice.payload.SubscriptionPageResponse;
import com.mrthinkj.userservice.payload.UserResponseDTO;
import com.mrthinkj.userservice.service.SubscriptionService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.mrthinkj.core.utils.WebUtils.*;

@RestController
@RequestMapping("/api/v1/subscriptions")
@AllArgsConstructor
public class SubscriptionController {
    SubscriptionService subscriptionService;
    @PostMapping("/subscribe/{targetId}")
    public ResponseEntity<SubscriptionDTO> subscribe(@PathVariable Long targetId,
                                                     @RequestHeader("username") String username){
        return new ResponseEntity<>(subscriptionService.subscribe(
                targetId, username), HttpStatus.CREATED);
    }

    @DeleteMapping("/unsubscribe/{targetId}")
    public ResponseEntity<?> unsubscribe(@PathVariable Long targetId,
                                         @RequestHeader("username") String username){
        subscriptionService.unsubscribe(targetId, username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/users/{userId}/subscribers")
    public ResponseEntity<SubscriptionPageResponse> getAllSubscriberByPublisherId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = DEFAULT_PAGE_NUM) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = DEFAULT_SORT_DIR) String sortDir){
        return ResponseEntity.ok(subscriptionService.getAllSubscriberByPublisherId(userId, page, size, sortBy, sortDir));
    }

    @GetMapping("/users/{userId}/subscriberIds")
    public ResponseEntity<List<Long>> getAllSubscriberIdsByPublisherId(@PathVariable Long userId){
        return ResponseEntity.ok(subscriptionService.getAllSubscriberIdsByPublisherId(userId));
    }

    @GetMapping("/users/{userId}/subscriptions")
    public ResponseEntity<SubscriptionPageResponse> getAllSubscriptionByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = DEFAULT_PAGE_NUM) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = DEFAULT_SORT_DIR) String sortDir){
        return ResponseEntity.ok(subscriptionService.getAllPublisherBySubscriberId(userId, page, size, sortBy, sortDir));
    }

    @GetMapping("/users/{userId}/subscribers/count")
    public ResponseEntity<Integer> countSubscriberByPublisherId(@PathVariable Long userId){
        return ResponseEntity.ok(subscriptionService.countByPublisherId(userId));
    }

    @GetMapping("/users/{userId}/subscriptions/count")
    public ResponseEntity<Integer> countSubscriptionByUserId(@PathVariable Long userId){
        return ResponseEntity.ok(subscriptionService.countBySubscriberId(userId));
    }
}

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

@RestController
@RequestMapping("/api/v1/")
@AllArgsConstructor
public class SubscriptionController {
    SubscriptionService subscriptionService;
    @PostMapping("subscriptions/subscribe")
    public ResponseEntity<SubscriptionDTO> subscribe(@RequestBody SubscriptionDTO subscriptionDTO){
        return new ResponseEntity<>(subscriptionService.subscribe(
                subscriptionDTO.getSubscriberId(), subscriptionDTO.getPublisherId()), HttpStatus.CREATED);
    }

    @DeleteMapping("subscriptions/unsubscribe")
    public ResponseEntity<?> unsubscribe(@RequestBody SubscriptionDTO subscriptionDTO){
        subscriptionService.unsubscribe(subscriptionDTO.getSubscriberId(), subscriptionDTO.getPublisherId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/users/{userId}/subscribers")
    public ResponseEntity<SubscriptionPageResponse> getAllSubscriberByPublisherId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir){
        return ResponseEntity.ok(subscriptionService.getAllSubscriberByPublisherId(userId, page, size, sortBy, sortDir));
    }

    @GetMapping("/users/{userId}/subscriptions")
    public ResponseEntity<SubscriptionPageResponse> getAllSubscriptionByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir){
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

package com.mrthinkj.userservice.service;

import com.mrthinkj.userservice.payload.SubscriptionDTO;
import com.mrthinkj.userservice.payload.SubscriptionPageResponse;
import com.mrthinkj.userservice.payload.UserResponseDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubscriptionService {
    SubscriptionDTO subscribe(Long subscriberId, Long publisherId);
    void unsubscribe(Long subscriberId, Long publisherId);
    SubscriptionPageResponse getAllSubscriberByPublisherId(Long publisherId, int page, int size,
                                                           String sortBy, String sortDir);
    SubscriptionPageResponse getAllPublisherBySubscriberId(Long publisherId, int page, int size,
                                                           String sortBy, String sortDir);
    int countByPublisherId(Long publisherId);
    int countBySubscriberId(Long subscriberId);
    boolean isUserSubscribeTo(Long userId, Long publisherId);
}

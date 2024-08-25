package com.mrthinkj.userservice.service.impl;

import com.mrthinkj.userservice.entity.Subscription;
import com.mrthinkj.userservice.entity.User;
import com.mrthinkj.userservice.exception.ResourceNotFoundException;
import com.mrthinkj.userservice.payload.SubscriptionDTO;
import com.mrthinkj.userservice.payload.SubscriptionPageResponse;
import com.mrthinkj.userservice.payload.UserResponseDTO;
import com.mrthinkj.userservice.repository.SubscriptionRepository;
import com.mrthinkj.userservice.repository.UserRepository;
import com.mrthinkj.userservice.service.SubscriptionService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.LongFunction;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    SubscriptionRepository subscriptionRepository;
    UserRepository userRepository;

    @Override
    public SubscriptionDTO subscribe(Long targetId, String username) {
        User target = userRepository.findById(targetId).orElseThrow(
                ()-> new ResourceNotFoundException("User", "Id", targetId.toString())
        );
        User user = userRepository.findByUsernameOrEmail(username, username).orElseThrow(
                ()-> new ResourceNotFoundException("User", "Id", username)
        );
        Subscription subscription = new Subscription();
        subscription.setSubscriber(user);
        subscription.setPublisher(target);
        subscriptionRepository.save(subscription);
        return SubscriptionDTO.builder()
                .subscriberId(user.getId())
                .publisherId(targetId)
                .build();
    }

    @Override
    @Transactional
    public void unsubscribe(Long targetId, String username) {
        // TODO: Implement caching
        User user = userRepository.findByUsernameOrEmail(username, username).orElseThrow(
                ()-> new ResourceNotFoundException("User", "Id", username)
        );
        Subscription subscription = subscriptionRepository.findByPublisherIdAndSubscriberId(targetId, user.getId())
                .orElseThrow(()-> new RuntimeException(
                        String.format("User with id %s does not subscribe to user with id %s", targetId, user.getId())));
        subscriptionRepository.delete(subscription);
    }

    @Override
    public SubscriptionPageResponse getAllSubscriberByPublisherId(Long id, int page, int size,
                                                                  String sortBy, String sortDir) {
        Sort sort = Sort.by(sortDir.equalsIgnoreCase(
                Sort.Direction.ASC.name()) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page <User> usersPage = subscriptionRepository.findSubscriberByPublisherId(id, pageable);
        List<User> users = usersPage.getContent();
        return SubscriptionPageResponse.builder()
                .userResponseDTOS(users.stream().map(this::mapToResponseDTO).collect(Collectors.toList()))
                .pageNumber(usersPage.getNumber())
                .pageSize(usersPage.getSize())
                .totalPages(usersPage.getTotalPages())
                .totalElements(usersPage.getNumberOfElements())
                .isLastPage(usersPage.isLast())
                .build();
    }

    @Override
    public SubscriptionPageResponse getAllPublisherBySubscriberId(Long subscriberId, int page, int size,
                                                                  String sortBy, String sortDir) {
        Sort sort = Sort.by(sortDir.equalsIgnoreCase(
                Sort.Direction.ASC.name()) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> usersPage = subscriptionRepository.findPublisherBySubscriberId(subscriberId, pageable);
        List<User> users = usersPage.getContent();
        return SubscriptionPageResponse.builder()
                .userResponseDTOS(users.stream().map(this::mapToResponseDTO).collect(Collectors.toList()))
                .pageNumber(usersPage.getNumber())
                .pageSize(usersPage.getSize())
                .totalPages(usersPage.getTotalPages())
                .totalElements(usersPage.getNumberOfElements())
                .isLastPage(usersPage.isLast())
                .build();
    }

    @Override
    public List<Long> getAllSubscriberIdsByPublisherId(Long id) {
        return subscriptionRepository.findAllSubscriberIdsByPublisherId(id);
    }

    @Override
    public int countByPublisherId(Long publisherId) {
        return subscriptionRepository.countByPublisherId(publisherId);
    }

    @Override
    public int countBySubscriberId(Long subscriberId) {
        return subscriptionRepository.countBySubscriberId(subscriberId);
    }

    @Override
    public boolean isUserSubscribeTo(Long userId, Long publisherId) {
        return subscriptionRepository.existsBySubscriberIdAndPublisherId(userId, publisherId);
    }

    private UserResponseDTO mapToResponseDTO(User user){
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .profilePicture(user.getProfilePicture())
                .build();
    }
}

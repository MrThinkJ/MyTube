package com.mrthinkj.notificationservice.service;

import com.mrthinkj.core.entity.NewVideoNotification;
import com.mrthinkj.core.entity.NotificationEvent;
import com.mrthinkj.core.entity.NotificationType;
import com.mrthinkj.core.exception.ServiceUnavailableException;
import com.mrthinkj.notificationservice.entity.Notification;
import com.mrthinkj.notificationservice.payload.NotificationResponse;
import com.mrthinkj.notificationservice.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.mrthinkj.core.utils.APIUtils.USER_API;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService{
    NotificationRepository notificationRepository;
    private final Map<Long, SseEmitter> emitterMap = new HashMap<>();
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    WebClient.Builder webClientBuilder;
    @Override
    public SseEmitter addEmitter(Long userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitterMap.put(userId, emitter);
        emitter.onCompletion(()-> emitterMap.remove(userId));
        emitter.onTimeout(() -> emitterMap.remove(userId));
        return emitter;
    }

    @Override
    public void sendNotification(NotificationEvent notificationEvent) {
        switch (notificationEvent.getType()){
            case NEW_VIDEO -> sendNewVideoNotification((NewVideoNotification) notificationEvent.getNotification());
        }

    }

    private void sendNewVideoNotification(NewVideoNotification newVideoNotification){
        List<Long> subscriberIds = webClientBuilder.build()
                .get()
                .uri(String.format(USER_API+"/%s/subscriberIds",
                        newVideoNotification.getVideoOwnerId()))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    LOGGER.error("Client error: {}", clientResponse.statusCode());
                    throw new RuntimeException("Error with client");
                })
                .bodyToFlux(Long.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorMap(TimeoutException.class, ex->{
                    throw new ServiceUnavailableException("User service unavailable");
                })
                .doOnError(ex ->{
                    LOGGER.error("Exception throw: {}", ex.getMessage());
                })
                .collectList()
                .block();
        if(subscriberIds == null)
            return;
        subscriberIds.forEach(subscriberId ->{
            String message = "";
            Notification notification = new Notification();
            notification.setNotificationType(NotificationType.NEW_VIDEO);
            notification.setRead(false);
            notification.setMessage(message);
            notification.setToUserId(subscriberId);
            notification.setCreatedAt(LocalDateTime.now());
            Notification createdNotification = notificationRepository.save(notification);
            SseEmitter emitter = emitterMap.get(subscriberId);
            if (emitter != null){
                try {
                    emitter.send(SseEmitter.event().data(NotificationResponse.builder()
                            .id(createdNotification.getId())
                            .toUserId(subscriberId)
                            .message(message)
                            .createdAt(notification.getCreatedAt())
                            .isRead(createdNotification.isRead())
                            .notificationType(NotificationType.NEW_VIDEO)
                            .build()));
                } catch (IOException e){
                    LOGGER.error("Error when send SSE message: {}", e.getMessage());
                }
            }
        });
    }
}

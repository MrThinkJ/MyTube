package com.mrthinkj.notificationservice.service;

import com.mrthinkj.core.entity.NotificationEvent;
import com.mrthinkj.core.entity.NotificationType;
import com.mrthinkj.notificationservice.entity.Notification;
import com.mrthinkj.notificationservice.payload.NotificationResponse;
import com.mrthinkj.notificationservice.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService{
    NotificationRepository notificationRepository;
    private final Map<Long, SseEmitter> emitterMap = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
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
        Notification notification = new Notification();
        notification.setNotificationType(notificationEvent.getType());
        notification.setRead(false);
        notification.setMessage(notificationEvent.getMessage());
        notification.setUserId(notificationEvent.getUserId());
        notification.setCreatedAt(LocalDateTime.now());
        Notification createdNotification = notificationRepository.save(notification);
        SseEmitter emitter = emitterMap.get(notificationEvent.getUserId());
        if (emitter != null){
            try {
                emitter.send(SseEmitter.event().data(NotificationResponse.builder()
                                .id(createdNotification.getId())
                                .userId(notificationEvent.getUserId())
                                .message(notificationEvent.getMessage())
                                .createdAt(notification.getCreatedAt())
                                .isRead(createdNotification.isRead())
                                .notificationType(notificationEvent.getType())
                        .build()));
            } catch (IOException e){
                logger.error("Error when send SSE message: {}", e.getMessage());
            }
        }
    }
}

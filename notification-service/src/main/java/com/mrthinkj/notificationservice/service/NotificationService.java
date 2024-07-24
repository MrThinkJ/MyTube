package com.mrthinkj.notificationservice.service;

import com.mrthinkj.core.entity.NotificationEvent;
import com.mrthinkj.core.entity.NotificationType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {
    SseEmitter addEmitter(Long userId);
    void sendNotification(NotificationEvent notificationEvent);
}

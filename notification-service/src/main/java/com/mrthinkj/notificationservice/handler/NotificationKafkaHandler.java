package com.mrthinkj.notificationservice.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrthinkj.core.entity.NotificationEvent;
import com.mrthinkj.notificationservice.service.NotificationService;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "notification-events-topic", groupId = "notification-event")
public class NotificationKafkaHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final NotificationService notificationService;

    public NotificationKafkaHandler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaHandler
    public void handle(@Payload NotificationEvent notificationEvent) {
        try {
            LOGGER.info("Received new notification with id: {}",
                    notificationEvent.getId());
            notificationService.sendNotification(notificationEvent);
        } catch (Exception e) {
            LOGGER.error("Exception when processing notification event: {}", e.getMessage(), e);
        }
    }
}

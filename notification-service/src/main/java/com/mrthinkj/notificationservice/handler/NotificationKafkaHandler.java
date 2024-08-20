package com.mrthinkj.notificationservice.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrthinkj.core.entity.NotificationEvent;
import com.mrthinkj.notificationservice.service.NotificationService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@KafkaListener(topics = "notification-events-topic")
public class NotificationKafkaHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    NotificationService notificationService;

    @KafkaHandler
    public void handle(@Payload String message){
        ObjectMapper mapper = new ObjectMapper();
        try {
            NotificationEvent notificationEvent = mapper.readValue(message, NotificationEvent.class);
            LOGGER.info("Receive new notification with id: {}", notificationEvent.getId());
            notificationService.sendNotification(notificationEvent);
        } catch (Exception e){
            LOGGER.error("Exception when receive new notification event with exception: {}",
                     e.getMessage());
        }
    }

}

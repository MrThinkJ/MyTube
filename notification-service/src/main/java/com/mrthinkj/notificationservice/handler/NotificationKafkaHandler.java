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
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    NotificationService notificationService;
    @KafkaHandler
    public void handle(@Payload String message){
        ObjectMapper mapper = new ObjectMapper();
        try {
            NotificationEvent notificationEvent = mapper.readValue(message, NotificationEvent.class);
            logger.info("Receive new notification for user with id: {}", notificationEvent.getUserId());
            notificationService.sendNotification(notificationEvent);
            logger.info("Send notification with id {} to user with id {}",
                    notificationEvent.getId(), notificationEvent.getUserId());
        } catch (Exception e){
            logger.error("Exception when receive new notification event with exception: {}",
                     e.getMessage());
        }
    }

}

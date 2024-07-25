package com.mrthinkj.notificationservice.payload;

import com.mrthinkj.core.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponse {
    private Long id;
    private Long toUserId;
    private String message;
    private NotificationType notificationType;
    private boolean isRead;
    private LocalDateTime createdAt;
}

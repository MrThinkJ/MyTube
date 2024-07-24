package com.mrthinkj.core.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationEvent {
    private String id;
    private Long userId;
    private String message;
    private NotificationType type;
}

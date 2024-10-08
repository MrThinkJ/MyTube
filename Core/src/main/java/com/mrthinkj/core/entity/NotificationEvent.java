package com.mrthinkj.core.entity;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationEvent {
    private String id;
    private NewVideoNotification notification;
    private NotificationType type;
}

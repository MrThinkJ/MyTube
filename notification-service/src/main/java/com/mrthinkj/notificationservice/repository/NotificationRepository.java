package com.mrthinkj.notificationservice.repository;

import com.mrthinkj.notificationservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}

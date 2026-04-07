package com.iss.service;

import com.iss.dto.NotificationDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class NotificationService {

    private static final int MAX_NOTIFICATIONS = 50;

    private final AtomicLong idGenerator = new AtomicLong(1);
    private final CopyOnWriteArrayList<NotificationDto> notifications = new CopyOnWriteArrayList<>();

    public void addNotification(String type, String title, String message) {
        notifications.add(
                new NotificationDto(
                        idGenerator.getAndIncrement(),
                        type,
                        title,
                        message,
                        LocalDateTime.now()
                )
        );

        while (notifications.size() > MAX_NOTIFICATIONS) {
            notifications.remove(0);
        }
    }

    public List<NotificationDto> getNotifications() {
        return new ArrayList<>(notifications).stream()
                .sorted(Comparator.comparing(NotificationDto::getCreatedAt).reversed())
                .toList();
    }

    public void clearNotifications() {
        notifications.clear();
    }
}

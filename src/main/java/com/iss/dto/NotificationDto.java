package com.iss.dto;

import java.time.LocalDateTime;

public class NotificationDto {

    private Long id;
    private String type;
    private String title;
    private String message;
    private LocalDateTime createdAt;

    public NotificationDto(Long id, String type, String title, String message, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.message = message;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

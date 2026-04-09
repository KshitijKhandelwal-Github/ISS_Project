package com.iss;

import com.iss.dto.NotificationDto;
import com.iss.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationServiceTest {

    private NotificationService service;

    @BeforeEach
    void setUp() {
        service = new NotificationService();
    }

    // TEST 1: Add Notification
    @Test
    void shouldAddNotification() {
        service.addNotification("TYPE", "Title", "Message");

        List<NotificationDto> notifications = service.getNotifications();

        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0).getMessage()).isEqualTo("Message");
        assertThat(notifications.get(0).getTitle()).isEqualTo("Title");
        assertThat(notifications.get(0).getType()).isEqualTo("TYPE");
    }

    // TEST 2: Maintain Max Limit (50)
    @Test
    void shouldMaintainMaxLimitOf50Notifications() {
        for (int i = 1; i <= 55; i++) {
            service.addNotification("TYPE", "Title " + i, "Message " + i);
        }

        List<NotificationDto> notifications = service.getNotifications();

        assertThat(notifications).hasSize(50);

        // Oldest should be removed → Message 1–5 gone
        assertThat(notifications.stream()
                .anyMatch(n -> n.getMessage().equals("Message 1")))
                .isFalse();

        assertThat(notifications.stream()
                .anyMatch(n -> n.getMessage().equals("Message 6")))
                .isTrue();
    }

    // TEST 3: Sorted by Latest First
    @Test
    void shouldReturnNotificationsSortedByLatestFirst() throws InterruptedException {
        service.addNotification("TYPE", "Title1", "Message1");
        Thread.sleep(5); // ensure different timestamps
        service.addNotification("TYPE", "Title2", "Message2");

        List<NotificationDto> notifications = service.getNotifications();

        assertThat(notifications.get(0).getMessage()).isEqualTo("Message2");
        assertThat(notifications.get(1).getMessage()).isEqualTo("Message1");
    }

    // TEST 4: Clear Notifications
    @Test
    void shouldClearNotifications() {
        service.addNotification("TYPE", "Title", "Message");

        service.clearNotifications();

        List<NotificationDto> notifications = service.getNotifications();

        assertThat(notifications).isEmpty();
    }
}
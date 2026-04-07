package com.iss.notification.service;

import com.iss.notification.dto.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);

    public void sendEmail(NotificationEvent notificationEvent) {
        logger.info(
                "Email notification prepared. to={}, subject={}, message={}",
                notificationEvent.getRecipientEmail(),
                notificationEvent.getSubject(),
                notificationEvent.getMessage()
        );
    }
}

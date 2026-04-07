package com.iss.notification.kafka;

import com.iss.notification.dto.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.notifications.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class NotificationKafkaListener {

    private static final Logger logger = LoggerFactory.getLogger(NotificationKafkaListener.class);

    @KafkaListener(
            topics = "${app.notifications.kafka.topic}",
            groupId = "${spring.kafka.consumer.group-id:iss-notification-service}"
    )
    public void onNotification(NotificationEvent notificationEvent) {
        logger.info(
                "Real-time notification received from Kafka. type={}, referenceType={}, referenceId={}, subject={}",
                notificationEvent.getType(),
                notificationEvent.getReferenceType(),
                notificationEvent.getReferenceId(),
                notificationEvent.getSubject()
        );
    }
}

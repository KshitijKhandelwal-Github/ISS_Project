package com.iss.notification.kafka;

import com.iss.notification.dto.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.notifications.kafka.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class NotificationKafkaPublisher {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    private final String topicName;

    public NotificationKafkaPublisher(KafkaTemplate<String, NotificationEvent> kafkaTemplate,
                                      @Value("${app.notifications.kafka.topic}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public void publish(NotificationEvent notificationEvent) {
        kafkaTemplate.send(topicName, notificationEvent.getReferenceType(), notificationEvent)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.warn("Failed to publish notification to Kafka. topic={}, type={}, referenceId={}",
                                topicName,
                                notificationEvent.getType(),
                                notificationEvent.getReferenceId(),
                                ex);
                        return;
                    }
                    log.info("Published notification to Kafka. topic={}, type={}, referenceId={}",
                            topicName,
                            notificationEvent.getType(),
                            notificationEvent.getReferenceId());
                });
    }
}

package com.shibana.identity_service.message.producer;

import com.shibana.common.events.base.EventEnvelop;
import com.shibana.common.events.notification.WelcomeEmailRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class NotificationEventPublisher {
    KafkaTemplate<String, Object> kafkaTemplate;

    @NonFinal
    @Value("${infra.kafka.producer.name}")
    String PRODUCER_NAME;

    @NonFinal
    @Value("${infra.kafka.topics.notification.welcome-email-requested}")
    String WELCOME_EMAIL_TOPIC;

    public void publishWelcomeEmailEvent(
            String name,
            String email
    ) {
        WelcomeEmailRequestedEvent event = new WelcomeEmailRequestedEvent(name, email);
        EventEnvelop<WelcomeEmailRequestedEvent> envelop = new EventEnvelop<>(PRODUCER_NAME, event);

        kafkaTemplate.send(WELCOME_EMAIL_TOPIC, envelop);
    }
}

package com.shibana.notification_service.message.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shibana.common.events.base.EventEnvelop;
import com.shibana.common.events.notification.WelcomeEmailRequestedEvent;
import com.shibana.notification_service.dto.request.BrevoRecipient;
import com.shibana.notification_service.dto.request.SendEmailRequest;
import com.shibana.notification_service.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WelcomeEmailConsumer {
    ObjectMapper objectMapper;
    EmailService emailService;

    @KafkaListener(topics = "${app.kafka.topics.notification.welcome-email-requested}")
    public void onMessage(EventEnvelop<?> envelop) {
        if(envelop == null) {
            log.warn("Received null envelop");
            return;
        }
        WelcomeEmailRequestedEvent eventPayload = objectMapper.convertValue(envelop.getPayload(), WelcomeEmailRequestedEvent.class);
        BrevoRecipient recipient = BrevoRecipient.builder()
                .email(eventPayload.getEmail())
                .name(eventPayload.getName())
                .build();
        SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                .htmlContent("<html><head>Welcome to ShibaNa Net</head><body><p>Hello,</p>This is my first transactional email sent from Brevo.</p></body></html>")
                .subject("Welcome to ShibaNa Net")
                .recipientList(List.of(recipient))
                .build();
        log.info("Sending email to:: {}", eventPayload.getName());
        emailService.sendEmail(sendEmailRequest);
        log.info("Email sent successfully");
    }
}

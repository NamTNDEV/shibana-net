package com.shibana.notification_service.service;

import com.shibana.notification_service.dto.request.BrevoSendEmailRequest;
import com.shibana.notification_service.dto.request.BrevoSender;
import com.shibana.notification_service.dto.request.SendEmailRequest;
import com.shibana.notification_service.exception.AppException;
import com.shibana.notification_service.exception.ErrorCode;
import com.shibana.notification_service.repo.httpClient.BrevoEmailClient;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class EmailService {
    BrevoEmailClient brevoEmailClient;

    @NonFinal
    @Value("${service.email.brevo.api-key}")
    String API_KEY;

    @NonFinal
    @Value("${service.email.brevo.sender-email}")
    String SENDER_EMAIL;

    @NonFinal
    @Value("${service.email.brevo.sender-name}")
    String SENDER_NAME;

    public void sendEmail(SendEmailRequest request) {
        log.info("Email is sending...");

        BrevoSender sender = BrevoSender.builder()
                .email(SENDER_EMAIL)
                .name(SENDER_NAME)
                .build();

//        BrevoRecipient recipientSample = BrevoRecipient.builder()
//                .email("shibanatest@yopmail.com")
//                .name("ShibaNa Test")
//                .build();


        BrevoSendEmailRequest brevoSendEmailRequest = BrevoSendEmailRequest.builder()
                .htmlContent(request.getHtmlContent())
                .sender(sender)
                .subject(request.getSubject())
                .to(request.getRecipientList())
                .build();

        try {
            brevoEmailClient.sendEmail(API_KEY, brevoSendEmailRequest);
            log.info("Email sent successfully.");
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage());
            throw new AppException(ErrorCode.SEND_EMAIL_FAILED);
        }
    }
}

package com.shibana.notification_service.controller;

import com.shibana.notification_service.dto.request.SendEmailRequest;
import com.shibana.notification_service.dto.response.ApiResponse;
import com.shibana.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class EmailController {
    EmailService emailService;

    @PostMapping("/send-email")
    public ApiResponse<Void> sendEmail(@RequestBody SendEmailRequest request) {
        log.info("Send email endpoint called");
        emailService.sendEmail(request);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Email sent successfully")
                .build();
    }
}

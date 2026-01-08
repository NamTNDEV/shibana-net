package com.shibana.notification_service.repo.httpClient;

import com.shibana.notification_service.dto.request.BrevoSendEmailRequest;
import com.shibana.notification_service.dto.response.BrevoSendEmailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "brevo-email-client", url = "https://api.brevo.com")
public interface BrevoEmailClient {
    @PostMapping(value = "/v3/smtp/email", consumes = MediaType.APPLICATION_JSON_VALUE)
    BrevoSendEmailResponse sendEmail(
            @RequestHeader("api-key") String apiKey,
            BrevoSendEmailRequest request
    );
}

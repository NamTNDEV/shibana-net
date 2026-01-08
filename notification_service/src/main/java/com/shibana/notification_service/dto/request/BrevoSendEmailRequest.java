package com.shibana.notification_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrevoSendEmailRequest {
    String htmlContent;
    BrevoSender sender;
    String subject;
    List<BrevoRecipient> to;
}

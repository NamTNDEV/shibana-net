package com.shibana.post_service.messaging.listener;

import com.shibana.post_service.messaging.dto.MessageType;
import com.shibana.post_service.messaging.dto.payloads.AvatarUpdatedPayload;
import com.shibana.post_service.messaging.helper.JsonHelper;
import com.shibana.post_service.service.PostEventSyncService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class SocialEventListener {
    JsonHelper jsonHelper;
    PostEventSyncService postEventSyncService;

    @KafkaListener(
            topics = "${infra.kafka.topics.avatar-updated}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handleAvatarUpdatedEvent(List<String> rawJsonList) {
        List<AvatarUpdatedPayload> payloads = new ArrayList<>();

        for (String rawJson : rawJsonList) {
            try {
                MessageType messageType = jsonHelper.extractEventType(rawJson);
                if (messageType == MessageType.AVATAR_UPDATED) {
                    AvatarUpdatedPayload payload = jsonHelper.parsePayload(rawJson, AvatarUpdatedPayload.class);
                    if (!payload.isValid()) {
                        log.warn(":: ⚠️ Phát hiện Payload thiếu dữ liệu, loại bỏ! Dữ liệu: {} ::", rawJson);
                        continue;
                    }
                    payloads.add(payload);
                }
            } catch (Exception e) {
                log.warn(":: ⚠️ Lỗi Parse JSON, loại bỏ 1 tin nhắn. Chi tiết: {} ::", e.getMessage());
            }
        }

        if (!payloads.isEmpty()) {
            postEventSyncService.syncAuthorAvatar(payloads);
        }

    }
}

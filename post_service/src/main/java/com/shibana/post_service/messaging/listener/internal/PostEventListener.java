package com.shibana.post_service.messaging.listener.internal;

import com.shibana.post_service.messaging.dto.EventType;
import com.shibana.post_service.messaging.dto.payloads.ReactedPayload;
import com.shibana.post_service.messaging.helper.JsonHelper;
import com.shibana.post_service.service.ReactionService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class PostEventListener {
    JsonHelper jsonHelper;
    ReactionService reactionService;

    @KafkaListener(
            topics = "${infra.kafka.topics.reaction-event.name}",
            groupId = "${infra.kafka.group-ids.reaction-sync-db}",
            containerFactory = "microBatchFactory")
    public void handleReactionEvents(List<ConsumerRecord<String, String>> rawJsonEvents) {
        log.info("🚚 [Sync DB Flow] Vừa kéo về một lưới gồm {} reaction events.", rawJsonEvents.size());
        Map<EventType, List<ConsumerRecord<String, String>>> groupedEvents = rawJsonEvents.stream()
                .collect(Collectors.groupingBy(event -> jsonHelper.extractEventType(event.value())));
        groupedEvents.forEach((eventType, records) -> {
            switch (eventType) {
                case USER_REACTED -> handleReactionToggleBatch(records);
                default ->
                        log.warn("⚠ Found unsupported event type [{}] in batch with {} records. Ignored!", eventType, records.size());
            }
        });
    }

    private void handleReactionToggleBatch(List<ConsumerRecord<String, String>> records) {
        List<ReactedPayload> allEvents = records.stream()
                .map(record -> jsonHelper.parsePayload(record.value(), ReactedPayload.class))
                .toList();
        reactionService.batchUpsertToDb(allEvents);
    }
}

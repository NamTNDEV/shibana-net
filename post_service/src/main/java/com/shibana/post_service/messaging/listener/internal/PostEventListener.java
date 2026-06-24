package com.shibana.post_service.messaging.listener.internal;

import com.shibana.post_service.messaging.helper.JsonHelper;
import com.shibana.post_service.service.AuthorService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class PostEventListener {
    JsonHelper jsonHelper;
    AuthorService authorService;

    @KafkaListener(
            topics = "${infra.kafka.topics.reaction-event.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "batchFactory")
    public void handleReactionEvents(List<ConsumerRecord<String, String>> rawJsonEvents) {
        log.info("[handleReactionEvents]::Received reaction event: {}", rawJsonEvents);
    }
}

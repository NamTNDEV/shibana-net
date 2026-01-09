package com.shibana.notification_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class TestController {
    @GetMapping("/test")
    public String test() {
        log.info("Test endpoint called");
        return "Notification Service is up and running!";
    }

    @KafkaListener(topics = "hello-world-topic", groupId = "notification_service_group")
    public void listenHelloWorldTopic(String message) {
        log.info("Received message from hello-world-topic:: {}", message);
    }
}

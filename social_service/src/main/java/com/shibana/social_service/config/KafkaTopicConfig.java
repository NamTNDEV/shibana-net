package com.shibana.social_service.config;

import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class KafkaTopicConfig {
    @Value("${infra.kafka.topics.avatar-updated}")
    String avatarUpdatedTopic;

    @Bean
    public NewTopic avatarUpdatedTopic() {
        return TopicBuilder.name(avatarUpdatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}

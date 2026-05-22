package com.shibana.identity_service.config;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaTopicConfig {
    @Value("${infra.kafka.replication-factor-default}")
    int replicationFactorDefault;

    @Value("${infra.kafka.topic.user-event.name}")
    String userEventsTopic;

    @Value("${infra.kafka.topic.user-event.partitions}")
    int userEventsTopicPartitions;

    @Bean
    public NewTopic userEventsTopic() {
        return TopicBuilder.name(userEventsTopic)
                .partitions(userEventsTopicPartitions)
                .replicas(replicationFactorDefault)
                .build();
    }
}

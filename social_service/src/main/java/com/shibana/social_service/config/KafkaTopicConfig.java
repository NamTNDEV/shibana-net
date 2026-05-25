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
    @Value("${infra.kafka.replication-factor-default}")
    int replicationFactorDefault;

    @Value("${infra.kafka.topics.profile-event.name}")
    String profileEventsTopic;

    @Value("${infra.kafka.topics.profile-event.partitions}")
    int profileEventsTopicPartitions;

    @Bean
    public NewTopic profileEventsTopic() {
        return TopicBuilder.name(profileEventsTopic)
                .partitions(profileEventsTopicPartitions)
                .replicas(replicationFactorDefault)
                .build();
    }
}

package com.shibana.post_service.config;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaTopicConfig {
    @Value("${infra.kafka.replication-factor-default}")
    int replicationFactorDefault;

    @Value("${infra.kafka.topics.reaction-event.name}")
    String reactionEventsTopic;

    @Value("${infra.kafka.topics.reaction-event.partitions}")
    int reactionEventsTopicPartitions;

    @Bean
    public NewTopic reactionEventsTopic() {
        return TopicBuilder.name(reactionEventsTopic)
                .partitions(reactionEventsTopicPartitions)
                .replicas(replicationFactorDefault)
                .build();
    }
}

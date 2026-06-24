package com.shibana.post_service.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Primary
    @Bean("defaultKafkaTemplate")
    public KafkaTemplate<Object, Object> defaultKafkaTemplate(ProducerFactory<Object, Object> defaultProducerFactory) {
        return new KafkaTemplate<>(defaultProducerFactory);
    }

    @Bean("reactionKafkaTemplate")
    public KafkaTemplate<Object, Object> reactionKafkaTemplate(
            ProducerFactory<Object, Object> defaultProducerFactory) {

        Map<String, Object> props = new HashMap<>(
                ((DefaultKafkaProducerFactory<Object, Object>) defaultProducerFactory).getConfigurationProperties()
        );

        props.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 32768);

        ProducerFactory<Object, Object> customProducerFactory = new DefaultKafkaProducerFactory<>(props);

        return new KafkaTemplate<>(customProducerFactory);
    }
}

package com.shibana.post_service.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration

public class KafkaConfig {
    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
        FixedBackOff fixedBackOff = new FixedBackOff(5000L, 3);

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template,
                (consumerRecord, ex) -> {
                    log.error(":: 🚑 Hết thuốc chữa! Chuyển tin nhắn vào DLQ. Topic gốc: {}, Lỗi: {} ::",
                            consumerRecord.topic(), ex.getMessage());
                    // Nó sẽ tự động tạo tên topic là: <topic-gốc>.DLT (Ví dụ: avatar-updated.DLT)
                    return new TopicPartition(consumerRecord.topic() + ".DLT", consumerRecord.partition());
                }
        );

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, fixedBackOff);
        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class); // Ví dụ: lỗi này không nên retry, có thể do dữ liệu sai

        return errorHandler;
    }
}

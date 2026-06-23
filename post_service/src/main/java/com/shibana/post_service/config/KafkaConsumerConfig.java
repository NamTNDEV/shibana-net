package com.shibana.post_service.config;

import com.shibana.post_service.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
public class KafkaConsumerConfig {
    @Bean("batchFactory") // Đặt tên Bean để xài riêng
    public ConcurrentKafkaListenerContainerFactory<?, ?> batchFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> kafkaConsumerFactory) {

        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();

        // 1. Kế thừa toàn bộ cấu hình gốc từ YML (bao gồm max-poll-records)
        configurer.configure(factory, kafkaConsumerFactory);

        // 2. 🚀 CHỈ ĐỊNH RIÊNG cái Factory này phải chạy ở chế độ BATCH
        factory.setBatchListener(true);

        return factory;
    }

    @Bean
    public CommonErrorHandler kafkaErrorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, e) -> new TopicPartition(record.topic() + ".DLT", record.partition())
        );

        FixedBackOff defaultBackOff = new FixedBackOff(2000L, 3L);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, defaultBackOff);

        errorHandler.setBackOffFunction((record, ex) -> {
            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
            if ((cause instanceof AppException appEx) && !(appEx.getErrorCode().isRetryable())) {
                log.warn("❌ Phát hiện lỗi dữ liệu [{}]. Cấm Retry, sút thẳng sang DLT!", appEx.getErrorCode().name());
                return new FixedBackOff(0L, 0L);
            }
            return defaultBackOff;
        });
        return errorHandler;
    }
}

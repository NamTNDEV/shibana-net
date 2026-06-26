package com.shibana.post_service.config;

import com.shibana.post_service.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConsumerConfig {
    @Bean("microBatchFactory")
    public ConcurrentKafkaListenerContainerFactory<?, ?> batchFactory(
            ConsumerFactory<Object, Object> kafkaConsumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();

        Map<String, Object> consumerProps = new HashMap<>(((DefaultKafkaConsumerFactory<Object, Object>) kafkaConsumerFactory).getConfigurationProperties());
        consumerProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 2000);
        consumerProps.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 2000);
        consumerProps.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 200000); //347B * 2k ~ 694KB ~ 678KB => (25%-30%) = ~ 200KB

        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(consumerProps));
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

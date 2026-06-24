package com.shibana.post_service.properties;

import com.shibana.post_service.model.enums.AggregateTypeEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "infra.kafka")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InfraKafkaProperties {
    Map<AggregateTypeEnum, String> outboxRouting;
    Map<AggregateTypeEnum, String> templateRouting;
}

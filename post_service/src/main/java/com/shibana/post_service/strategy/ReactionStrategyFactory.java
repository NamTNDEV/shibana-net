package com.shibana.post_service.strategy;

import com.shibana.post_service.exception.AppException;
import com.shibana.post_service.exception.ErrorCode;
import com.shibana.post_service.model.enums.ReactionTargetTypeEnum;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ReactionStrategyFactory {
    Map<ReactionTargetTypeEnum, ReactionStrategy> strategyMap;

    public ReactionStrategyFactory (List<ReactionStrategy> reactionStrategyList) {
        strategyMap = reactionStrategyList.stream()
                .collect(Collectors.toMap(ReactionStrategy::getTargetType, Function.identity()));
    }

    public ReactionStrategy getStrategy(ReactionTargetTypeEnum reactionTargetTypeEnum) {
        return Optional.ofNullable(strategyMap.get(reactionTargetTypeEnum))
                .orElseThrow(() -> new AppException(ErrorCode.REACT_TARGET_TYPE_NOT_SUPPORTED));
    }
}

package com.shibana.common.events.base;

public record EventEnvelop<T>(
        String producer,
        T payload
) {
    public T getPayload() {
        return payload;
    }
}

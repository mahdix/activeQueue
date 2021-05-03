package com.mahdix.activeQueue.model;

import com.mahdix.activeQueue.enums.ThreadMessageType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ThreadMessage<T> {
    private final Optional<T> data;
    private final ThreadMessageType messageType;

    public static <U> ThreadMessage<U> ofRequestTerminate() {
        return new ThreadMessage<>(Optional.empty(), ThreadMessageType.REQUEST_TERMINATE);
    }

    public static <U> ThreadMessage<U> ofRequestStart() {
        return new ThreadMessage<>(Optional.empty(), ThreadMessageType.REQUEST_START);
    }

    public static <U> ThreadMessage<U> ofData(U data) {
        return new ThreadMessage<>(Optional.of(data), ThreadMessageType.DATA);
    }
}

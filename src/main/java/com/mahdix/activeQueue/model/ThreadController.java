package com.mahdix.activeQueue.model;

import com.mahdix.activeQueue.enums.ThreadState;
import com.mahdix.activeQueue.fnint.CheckedConsumer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Supplier;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ThreadController<T> {
    private final Supplier<ThreadState> getState;
    private final CheckedConsumer<ThreadMessage<T>> sendMessageFn;

    public static <T> ThreadController of(Supplier<ThreadState> getState,
                            CheckedConsumer<ThreadMessage<T>> sendMessageFn) {
        return new ThreadController(getState, sendMessageFn);
    }

    //TODO: add a helper function to send a message to a thread
}

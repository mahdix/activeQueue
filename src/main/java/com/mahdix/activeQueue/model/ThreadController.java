package com.mahdix.activeQueue.model;

import com.mahdix.activeQueue.enums.ThreadState;
import com.mahdix.activeQueue.fnint.CheckedConsumer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Supplier;

@SuppressWarnings("unused")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ThreadController<T> {
    private final Supplier<ThreadState> getState;
    private final CheckedConsumer<ThreadMessage<T>> sendMessageFn;

    public static <T> ThreadController of(Supplier<ThreadState> getState,
                            CheckedConsumer<ThreadMessage<T>> sendMessageFn) {
        return new ThreadController(getState, sendMessageFn);
    }

    public void sendMessage(T msg) throws Exception {
        sendMessageFn.consume(ThreadMessage.ofData(msg));
    }
}

package com.mahdix.activeQueue.fnint;

@FunctionalInterface
public interface CheckedConsumer<T> {
    void consume(T data) throws Exception;
}

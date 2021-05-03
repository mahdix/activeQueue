package com.mahdix.activeQueue.fnint;

import com.mahdix.activeQueue.model.Properties;
import org.slf4j.Logger;

@FunctionalInterface
public interface ActiveFunction<T> {
    void apply(T workItem, CheckedConsumer<T> onNewDataMessageHandler, Properties stats, Logger logger) throws Exception;
}

package com.mahdix.activeQueue.fnint;

import com.mahdix.activeQueue.model.Properties;
import org.slf4j.Logger;

@FunctionalInterface
public interface ThreadWorkerFunction<T> {
    boolean apply(CheckedConsumer<T> onNewMessageHandler, Properties stats, Logger logger) throws Exception;
}

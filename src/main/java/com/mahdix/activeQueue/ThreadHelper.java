package com.mahdix.activeQueue;

import com.mahdix.activeQueue.enums.ThreadMessageType;
import com.mahdix.activeQueue.enums.ThreadState;
import com.mahdix.activeQueue.fnint.ActiveFunction;
import com.mahdix.activeQueue.fnint.CheckedConsumer;
import com.mahdix.activeQueue.fnint.ThreadWorkerFunction;
import com.mahdix.activeQueue.model.Properties;
import com.mahdix.activeQueue.model.ThreadController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("unused")
public class ThreadHelper {
    public static final Runnable NOOP = () -> {
    };

    private static final int REPORT_SECONDS = 10;

    public static <T> ThreadController<T> createSinkQueueThread(
            String threadName,
            ActiveFunction<T> workItemProcessor) {
        BlockingQueue<T> inputQueue = new LinkedBlockingQueue<>();

        return createThread(
                threadName,
                inputQueue::add,
                (onNewDataMessageHandler, stats, logger) -> {
                    var workItem = Optional.ofNullable(inputQueue.poll(100, TimeUnit.MILLISECONDS));

                    if (workItem.isPresent()) {
                        workItemProcessor.apply(workItem.get(), onNewDataMessageHandler, stats, logger);
                    }

                    return true;
                },
                NOOP
        );
    }

    public static <T> ThreadController<T> createThread(
            String name,
            CheckedConsumer<T> onNewDataMessageHandler,
            ThreadWorkerFunction<T> workerFunction,
            Runnable terminationHandler) {

        AtomicReference<ThreadState> state = new AtomicReference<>(ThreadState.CREATED);
        ThreadController<T> controller = ThreadController.<T>of(state::get, msg -> {
            if (msg.getMessageType() == ThreadMessageType.REQUEST_START) {
                state.set(ThreadState.RUNNING);
            } else if (msg.getMessageType() == ThreadMessageType.REQUEST_TERMINATE) {
                state.set(ThreadState.TERMINATING);
            } else if (msg.getMessageType() == ThreadMessageType.DATA) {
                onNewDataMessageHandler.consume(msg.getData().get());
            }
        });

        Thread thread = new Thread(() -> {
            Thread.currentThread().setName(name);
            Logger logger = getLogger(name);

            while (state.get() == ThreadState.CREATED) {
                safeSleep(100);
            }

            long startTime = Instant.now().getEpochSecond();

            long reportTime = Instant.now().getEpochSecond() + REPORT_SECONDS;
            Properties stats = Properties.of();

            while (true) {
                if (state.get() == ThreadState.TERMINATING) {
                    break;
                }

                if (Instant.now().getEpochSecond() >= reportTime) {
                    long duration = Instant.now().getEpochSecond() - startTime;
                    stats.put("uptime (sec)", duration);
                    reportStats(stats, logger);

                    reportTime += REPORT_SECONDS;
                }

                boolean canContinue = false;

                try {
                    canContinue = workerFunction.apply(onNewDataMessageHandler, stats, logger);
                } catch (Exception exc) {
                    stats.put("exceptionCount", (int) stats.getOrDefault("exceptionCount", 0) + 1);
                    logger.error("Error running worker function for " + name, exc);
                }

                if (!canContinue) break;
            }

            state.set(ThreadState.TERMINATED);
            terminationHandler.run();
        });

        thread.start();

        return controller;
    }

    private static void reportStats(Properties stats, Logger logger) {
        if (stats.size() > 0) {
            logger.info("{}", stats);
        }
    }

    public static void waitForThreadTermination(ThreadController<?> controller) {
        while (controller.getGetState().get() != ThreadState.TERMINATED) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    public static void safeSleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
        }
    }

    public static Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }
}
